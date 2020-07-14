package fi.hel.allu.servicecore.util;

import fi.hel.allu.servicecore.domain.GeometryComplexity;
import fi.hel.allu.servicecore.domain.ShouldSimplifyWithMinZoomLevel;
import fi.hel.allu.servicecore.domain.ZoomLevelSizeBounds;
import org.geolatte.geom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GeometrySimplifier {
  private static final Logger logger = LoggerFactory.getLogger(GeometrySimplifier.class);

  /**
   * Creates a list of bounds to help ensure only necessary geometries are made.
   */
  public static List<ZoomLevelSizeBounds> generateZoomLevelSizeBoundsList() {
    List<ZoomLevelSizeBounds> zoomLevelSizeBoundsList = new ArrayList<>();
    zoomLevelSizeBoundsList.add(new ZoomLevelSizeBounds(1, 3, null, 500, GeometryComplexity.POINT));
    zoomLevelSizeBoundsList.add(new ZoomLevelSizeBounds(1, 3, 500, 1000, GeometryComplexity.SIMPLE));
    zoomLevelSizeBoundsList.add(new ZoomLevelSizeBounds(1, 3, 1000, null, GeometryComplexity.FULL));
    zoomLevelSizeBoundsList.add(new ZoomLevelSizeBounds(4, 5, null, 100, GeometryComplexity.POINT));
    zoomLevelSizeBoundsList.add(new ZoomLevelSizeBounds(4, 5, 100, 500, GeometryComplexity.SIMPLE));
    zoomLevelSizeBoundsList.add(new ZoomLevelSizeBounds(4, 5, 500, null, GeometryComplexity.FULL));
    zoomLevelSizeBoundsList.add(new ZoomLevelSizeBounds(6, 8, null, 30, GeometryComplexity.POINT));
    zoomLevelSizeBoundsList.add(new ZoomLevelSizeBounds(6, 8, 30, 100, GeometryComplexity.SIMPLE));
    zoomLevelSizeBoundsList.add(new ZoomLevelSizeBounds(6, 8, 100, null, GeometryComplexity.FULL));
    // Only show full geometry when zoomLevel >= 9, Point type geometries need no check in this boundary
    zoomLevelSizeBoundsList.add(new ZoomLevelSizeBounds(9, 12, 0, null, GeometryComplexity.FULL));
    return zoomLevelSizeBoundsList;
  }

  /**
   *
   * @param geometry The geometry to be handled
   * @param complexity The complexity we expect
   * @param zoomLevelSizeBoundsList List of objects with zoom levels and bounds
   * @return Object saying if geometry needs simplification, the geometry's minimum zoom level for given complexity, and
   * a Geometry object for geometries of type POINT. That geometry can be a point of a polygon or the actual geometry,
   * depending on the geometry type.
   */
  public static ShouldSimplifyWithMinZoomLevel shouldSimplifyWithComplexity(
    Geometry geometry, Integer complexity, List<ZoomLevelSizeBounds> zoomLevelSizeBoundsList
  ) {
    if (!areEqualTypes(geometry, GeometryType.POINT)) {
      // Go here if the geometry type is not POINT

      if (zoomLevelSizeBoundsList == null) {
        zoomLevelSizeBoundsList = generateZoomLevelSizeBoundsList();
      }

      double boundingBoxSize = getBoundingBoxSize(geometry);
      logger.debug(
        "complexity: " + complexity +
          ", complexity name: " + GeometryComplexity.values()[complexity].name() +
          ", bounding box size (rounded up): " + Math.ceil(boundingBoxSize));
      // Get the smallest zoom where boundaries are fulfilled
      ZoomLevelSizeBounds zoomLevelSizeBounds = zoomLevelSizeBoundsList.stream()
        .filter(bounds -> bounds.getComplexity().ordinal() == complexity && bounds.isWithinBounds(boundingBoxSize))
        .min(Comparator.comparingInt(ZoomLevelSizeBounds::getMinZoomLevel))
        .orElse(null);
      if (zoomLevelSizeBounds == null) {
        logger.debug("Could not find suitable bounds for geometry, complexity: " +
          GeometryComplexity.values()[complexity].name() +
          ", bounding box size (rounded up): " + Math.ceil(boundingBoxSize));
        if (GeometryComplexity.FULL.ordinal() == complexity) {
          logger.debug("Using full geometry"); // Calling function should at least have full geometry in memory
          return new ShouldSimplifyWithMinZoomLevel(false, 1, null);
        }
        else {
          logger.debug("Returning null");
          // Expecting to have at least full geometry saved. Simplifications are used for improved UX.
          return null;
        }
      }

      if (zoomLevelSizeBounds.getComplexity().equals(GeometryComplexity.FULL)) {
        logger.debug("Full geo: min zoom level: " + zoomLevelSizeBounds.getMinZoomLevel() +
          ", bbox size: " + Math.ceil(boundingBoxSize));
        return new ShouldSimplifyWithMinZoomLevel(false, zoomLevelSizeBounds.getMinZoomLevel(), null);
      } else if (zoomLevelSizeBounds.getComplexity().equals(GeometryComplexity.SIMPLE)) {
        logger.debug("Simple geo: min zoom level: " + zoomLevelSizeBounds.getMinZoomLevel() +
          ", bbox size: " + Math.ceil(boundingBoxSize));
        return new ShouldSimplifyWithMinZoomLevel(true, zoomLevelSizeBounds.getMinZoomLevel(), null);
      } else {
        logger.debug("Point geo: min zoom level: " + zoomLevelSizeBounds.getMinZoomLevel());
        if (areEqualTypes(geometry, GeometryType.POLYGON)) {
          return new ShouldSimplifyWithMinZoomLevel(
            false, zoomLevelSizeBounds.getMinZoomLevel(), replacePolygonWithCentroid(geometry)
          );
        } else if (areEqualTypes(geometry, GeometryType.LINE_STRING)) {
          return new ShouldSimplifyWithMinZoomLevel(
            false, zoomLevelSizeBounds.getMinZoomLevel(), replaceLineStringWithStartPoint(geometry)
          );
        }
        // If not polygon or line string, it should then be point or something close to that. So this is okay?
        return new ShouldSimplifyWithMinZoomLevel(false, zoomLevelSizeBounds.getMinZoomLevel(), geometry);
      }
    } else {
      // Go here if the geometry type is POINT
      if (GeometryComplexity.POINT.ordinal() == complexity) {
        logger.debug("Point geo: min zoom level: 1, complexity name: " + GeometryComplexity.values()[complexity].name());
        return new ShouldSimplifyWithMinZoomLevel(false, 1, geometry);
      }
      else {
        // No need to return point geometries to other complexities than POINT
        logger.debug("Point geo: complexity name: " + GeometryComplexity.values()[complexity].name());
        logger.debug("Returning null");
        return null;
      }
    }
  }

  private static boolean areEqualTypes(Geometry geometry, GeometryType geometryType) {
    GeometryCollection collection = (GeometryCollection) geometry;
    if (collection.getNumGeometries() == 0) {
      return false;
    }
    Geometry firstGeo = collection.getGeometryN(0);
    return firstGeo.getGeometryType().equals(geometryType);
  }

  private static Geometry replacePolygonWithCentroid(Geometry geometry) {
    GeometryCollection collection = (GeometryCollection) geometry;
    if (collection.getNumGeometries() == 0) {
      logger.debug("GeometryCollection does not contain any geometries!");
      return geometry;
    }
    Polygon firstGeo = (Polygon) collection.getGeometryN(0);

    return geometryToCollection(firstGeo.getCentroid());
  }

  private static Geometry replaceLineStringWithStartPoint(Geometry geometry) {
    GeometryCollection collection = (GeometryCollection) geometry;
    if (collection.getNumGeometries() == 0) {
      logger.debug("GeometryCollection does not contain any geometries!");
      return geometry;
    }
    LineString firstGeo = (LineString) collection.getGeometryN(0);

    return geometryToCollection(firstGeo.getStartPoint());
  }

  /**
   * An utility to create an GeometryCollection with a singular geometry, as location geometries are GeometryCollections.
   * @param geometry The geometry to set in a new GeometryCollection
   * @return A new GeometryCollection with the provided geometry inserted
   */
  public static GeometryCollection geometryToCollection(Geometry geometry) {
    Geometry[] geometries = new Geometry[1];
    geometries[0] = geometry;
    return new GeometryCollection(geometries);
  }

  private static double getBoundingBoxSize(Geometry geometry) {
    Envelope boundingBox = geometry.getEnvelope();

    Point bottomLeft = Points.create2D(boundingBox.getMinX(), boundingBox.getMinY());
    Point upperRight = Points.create2D(boundingBox.getMaxX(), boundingBox.getMaxY());
    return bottomLeft.distance(upperRight);
  }
}
