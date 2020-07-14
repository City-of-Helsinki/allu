package fi.hel.allu.servicecore.util;

import fi.hel.allu.servicecore.domain.ShouldSimplifyWithMinZoomLevel;
import fi.hel.allu.servicecore.domain.ZoomLevelSizeBounds;
import org.geolatte.geom.*;
import org.geolatte.geom.crs.CrsId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fi.hel.allu.servicecore.domain.GeometryComplexity.*;
import static fi.hel.allu.servicecore.util.GeometrySimplifier.*;
import static org.junit.jupiter.api.Assertions.*;

class GeometrySimplifierTest {
  private List<ZoomLevelSizeBounds> zoomLevelSizeBoundsList;

  @BeforeEach
  void setUp() {
    if (zoomLevelSizeBoundsList == null) {
      zoomLevelSizeBoundsList = generateZoomLevelSizeBoundsList();
    }
  }

  @Test
  void handleGeometrySize0() {
    Point point = new Point(PointSequenceBuilders.fixedSized(
      1, DimensionalFlag.d2D, new CrsId("EPSG", 2)
    ).add(2.549815796449239E7,6672928.049400462).toPointSequence());
    GeometryCollection geometries = geometryToCollection(point);

    ShouldSimplifyWithMinZoomLevel geoWithZoomFull = shouldSimplifyWithComplexity(geometries, FULL.ordinal(), zoomLevelSizeBoundsList);
    assertNull(geoWithZoomFull, "Object returned as non-null for complexity FULL");
    ShouldSimplifyWithMinZoomLevel geoWithZoomSimple = shouldSimplifyWithComplexity(geometries, SIMPLE.ordinal(), zoomLevelSizeBoundsList);
    assertNull(geoWithZoomSimple, "Object returned as non-null for complexity SIMPLE");
    ShouldSimplifyWithMinZoomLevel geoWithZoomPoint = shouldSimplifyWithComplexity(geometries, POINT.ordinal(), zoomLevelSizeBoundsList);
    assertNotNull(geoWithZoomPoint, "Object returned as null for complexity POINT");
    assertEquals(1, geoWithZoomPoint.getGeometry().getNumPoints());
    assertEquals(1, geoWithZoomPoint.getMinZoomLevel());
  }

  @Test
  void handleGeometrySize24() {
    // Create polygon with a bounding box size of about 24
    LinearRing[] linearRings = {new LinearRing(
      PointSequenceBuilders.fixedSized(4, DimensionalFlag.d2D, new CrsId("EPSG", 2))
        .add(2.549815796449239E7,6672928.049400462)
        .add(2.549815798458679E7,6672948.0474401545)
        .add(2.549817001625657E7,6672948.031487824)
        .add(2.549815796449239E7,6672928.049400462)
        .toPointSequence()
    )};
    GeometryCollection geometries = geometryToCollection(new Polygon(linearRings));

    ShouldSimplifyWithMinZoomLevel geoWithZoomFull = shouldSimplifyWithComplexity(geometries, FULL.ordinal(), zoomLevelSizeBoundsList);
    assertNotNull(geoWithZoomFull, "Object returned as null for complexity FULL");
    assertNull(geoWithZoomFull.getGeometry(), "No geometry should be returned for complexity FULL");
    assertEquals(9, geoWithZoomFull.getMinZoomLevel());
    ShouldSimplifyWithMinZoomLevel geoWithZoomSimple = shouldSimplifyWithComplexity(geometries, SIMPLE.ordinal(), zoomLevelSizeBoundsList);
    assertNull(geoWithZoomSimple, "Object returned as non-null for complexity SIMPLE");
    ShouldSimplifyWithMinZoomLevel geoWithZoomPoint = shouldSimplifyWithComplexity(geometries, POINT.ordinal(), zoomLevelSizeBoundsList);
    assertNotNull(geoWithZoomPoint, "Object returned as null for complexity POINT");
    assertEquals(1, geoWithZoomPoint.getGeometry().getNumPoints());
    assertEquals(1, geoWithZoomPoint.getMinZoomLevel());
  }

  @Test
  void handleGeometrySize52() {
    // Create polygon with a bounding box size of about 52
    LinearRing[] linearRings = {new LinearRing(
      PointSequenceBuilders.fixedSized(6, DimensionalFlag.d2D, new CrsId("EPSG", 2))
        .add(2.549815796449239E7,6672928.049400462)
        .add(2.5498157984586794E7,6672968.0474401545)
        .add(2.5498190016256575E7,6672968.031487824)
        .add(2.5498191016256575E7,6672968.031487824)
        .add(2.5498189996511605E7,6672928.0334480135)
        .add(2.549815796449239E7,6672928.049400462)
        .toPointSequence()
    )};
    GeometryCollection geometries = geometryToCollection(new Polygon(linearRings));

    ShouldSimplifyWithMinZoomLevel geoWithZoomFull = shouldSimplifyWithComplexity(geometries, FULL.ordinal(), zoomLevelSizeBoundsList);
    assertNotNull(geoWithZoomFull, "Object returned as null for complexity FULL");
    assertNull(geoWithZoomFull.getGeometry(), "No geometry should be returned for complexity FULL");
    assertEquals(9, geoWithZoomFull.getMinZoomLevel());
    ShouldSimplifyWithMinZoomLevel geoWithZoomSimple = shouldSimplifyWithComplexity(geometries, SIMPLE.ordinal(), zoomLevelSizeBoundsList);
    assertNotNull(geoWithZoomSimple, "Object returned as null for complexity SIMPLE");
    assertNull(geoWithZoomSimple.getGeometry(), "No geometry should be returned for complexity SIMPLE");
    assertEquals(6, geoWithZoomSimple.getMinZoomLevel());
    ShouldSimplifyWithMinZoomLevel geoWithZoomPoint = shouldSimplifyWithComplexity(geometries, POINT.ordinal(), zoomLevelSizeBoundsList);
    assertNotNull(geoWithZoomPoint, "Object returned as null for complexity POINT");
    assertEquals(1, geoWithZoomPoint.getGeometry().getNumPoints());
    assertEquals(1, geoWithZoomPoint.getMinZoomLevel());
  }

  @Test
  void handleGeometrySize95Square() {
    // Create polygon with a bounding box size of about 95
    LinearRing[] linearRings = {new LinearRing(
      PointSequenceBuilders.fixedSized(5, DimensionalFlag.d2D, new CrsId("EPSG", 2))
        .add(2.5496871495305188E7,6673159.105254234)
        .add(2.5496871523541775E7,6673192.195559442)
        .add(2.5496960507305935E7,6673192.120707662)
        .add(2.5496960479872487E7,6673159.030402)
        .add(2.5496871495305188E7,6673159.105254234)
        .toPointSequence()
    )};
    GeometryCollection geometries = geometryToCollection(new Polygon(linearRings));

    ShouldSimplifyWithMinZoomLevel geoWithZoomFull = shouldSimplifyWithComplexity(geometries, FULL.ordinal(), zoomLevelSizeBoundsList);
    assertNotNull(geoWithZoomFull, "Object returned as null for complexity FULL");
    assertNull(geoWithZoomFull.getGeometry(), "No geometry should be returned for complexity FULL");
    assertEquals(9, geoWithZoomFull.getMinZoomLevel());
    ShouldSimplifyWithMinZoomLevel geoWithZoomSimple = shouldSimplifyWithComplexity(geometries, SIMPLE.ordinal(), zoomLevelSizeBoundsList);
    assertNotNull(geoWithZoomSimple, "Object returned as null for complexity SIMPLE");
    assertNull(geoWithZoomSimple.getGeometry(), "No geometry should be returned for complexity SIMPLE");
    assertEquals(6, geoWithZoomSimple.getMinZoomLevel());
    ShouldSimplifyWithMinZoomLevel geoWithZoomPoint = shouldSimplifyWithComplexity(geometries, POINT.ordinal(), zoomLevelSizeBoundsList);
    assertNotNull(geoWithZoomPoint, "Object returned as null for complexity POINT");
    assertEquals(1, geoWithZoomPoint.getGeometry().getNumPoints());
    assertEquals(1, geoWithZoomPoint.getMinZoomLevel());
  }

  @Test
  void handleGeometrySize104() {
    // Create polygon with a bounding box size of about 104
    LinearRing[] linearRings = {new LinearRing(
      PointSequenceBuilders.fixedSized(7, DimensionalFlag.d2D, new CrsId("EPSG", 2))
        .add(2.5498161266645417E7,6673424.179695766)
        .add(2.5498159775779583E7,6673439.77857941)
        .add(2.54981758904441E7,6673475.20057745)
        .add(2.549817273132341E7,6673373.368609907)
        .add(2.549816702224182E7,6673389.86091641)
        .add(2.549816997522172E7,6673412.031075684)
        .add(2.5498161266645417E7,6673424.179695766)
        .toPointSequence()
    )};
    GeometryCollection geometries = geometryToCollection(new Polygon(linearRings));

    ShouldSimplifyWithMinZoomLevel geoWithZoomFull = shouldSimplifyWithComplexity(geometries, FULL.ordinal(), zoomLevelSizeBoundsList);
    assertNotNull(geoWithZoomFull, "Object returned as null for complexity FULL");
    assertNull(geoWithZoomFull.getGeometry(), "No geometry should be returned for complexity FULL");
    assertEquals(6, geoWithZoomFull.getMinZoomLevel());
    ShouldSimplifyWithMinZoomLevel geoWithZoomSimple = shouldSimplifyWithComplexity(geometries, SIMPLE.ordinal(), zoomLevelSizeBoundsList);
    assertNotNull(geoWithZoomSimple, "Object returned as null for complexity SIMPLE");
    assertNull(geoWithZoomSimple.getGeometry(), "No geometry should be returned for complexity SIMPLE");
    assertEquals(4, geoWithZoomSimple.getMinZoomLevel());
    ShouldSimplifyWithMinZoomLevel geoWithZoomPoint = shouldSimplifyWithComplexity(geometries, POINT.ordinal(), zoomLevelSizeBoundsList);
    assertNotNull(geoWithZoomPoint, "Object returned as null for complexity POINT");
    assertEquals(1, geoWithZoomPoint.getGeometry().getNumPoints());
    assertEquals(1, geoWithZoomPoint.getMinZoomLevel());
  }

  @Test
  void handleGeometrySize764() {
    // Create polygon with a bounding box size of about 764
    LinearRing[] linearRings = {new LinearRing(
      PointSequenceBuilders.fixedSized(11, DimensionalFlag.d2D, new CrsId("EPSG", 2))
        .add(2.54958865E7,6671301.999998963)
        .add(2.54958675E7,6671192.999998967)
        .add(2.54958485E7,6671194.999998967)
        .add(2.54956725E7,6670865.999998967)
        .add(2.54959335E7,6670728.999998969)
        .add(2.54959495E7,6670734.999998969)
        .add(2.54960795E7,6670969.999998965)
        .add(2.54960785E7,6670999.999998964)
        .add(2.54961765E7,6671187.999998966)
        .add(2.54960985E7,6671228.999998964)
        .add(2.54958865E7,6671301.999998963)
        .toPointSequence()
    )};
    GeometryCollection geometries = geometryToCollection(new Polygon(linearRings));

    ShouldSimplifyWithMinZoomLevel geoWithZoomFull = shouldSimplifyWithComplexity(geometries, FULL.ordinal(), zoomLevelSizeBoundsList);
    assertNotNull(geoWithZoomFull, "Object returned as null for complexity FULL");
    assertNull(geoWithZoomFull.getGeometry(), "No geometry should be returned for complexity FULL");
    assertEquals(4, geoWithZoomFull.getMinZoomLevel());
    ShouldSimplifyWithMinZoomLevel geoWithZoomSimple = shouldSimplifyWithComplexity(geometries, SIMPLE.ordinal(), zoomLevelSizeBoundsList);
    assertNotNull(geoWithZoomSimple, "Object returned as null for complexity SIMPLE");
    assertNull(geoWithZoomSimple.getGeometry(), "No geometry should be returned for complexity SIMPLE");
    assertEquals(1, geoWithZoomSimple.getMinZoomLevel());
    ShouldSimplifyWithMinZoomLevel geoWithZoomPoint = shouldSimplifyWithComplexity(geometries, POINT.ordinal(), zoomLevelSizeBoundsList);
    assertNull(geoWithZoomPoint, "Object returned as non-null for complexity POINT");
  }

  @Test
  void handleGeometrySize1086() {
    // Create polygon with a bounding box size of about 1086
    LinearRing[] linearRings = {new LinearRing(
      PointSequenceBuilders.fixedSized(6, DimensionalFlag.d2D, new CrsId("EPSG", 2))
        .add(2.54958865E7,6671301.999998963)
        .add(2.54958675E7,6671192.999998967)
        .add(2.54959335E7,6670728.999998969)
        .add(2.54959495E7,6670734.999998969)
        .add(2.54965985E7,6671530.999998964)
        .add(2.54958865E7,6671301.999998963)
        .toPointSequence()
    )};
    GeometryCollection geometries = geometryToCollection(new Polygon(linearRings));

    ShouldSimplifyWithMinZoomLevel geoWithZoomFull = shouldSimplifyWithComplexity(geometries, FULL.ordinal(), zoomLevelSizeBoundsList);
    assertNotNull(geoWithZoomFull, "Object returned as null for complexity FULL");
    assertNull(geoWithZoomFull.getGeometry(), "No geometry should be returned for complexity FULL");
    assertEquals(1, geoWithZoomFull.getMinZoomLevel());
    ShouldSimplifyWithMinZoomLevel geoWithZoomSimple = shouldSimplifyWithComplexity(geometries, SIMPLE.ordinal(), zoomLevelSizeBoundsList);
    assertNull(geoWithZoomSimple, "Object returned as non-null for complexity SIMPLE");
    ShouldSimplifyWithMinZoomLevel geoWithZoomPoint = shouldSimplifyWithComplexity(geometries, POINT.ordinal(), zoomLevelSizeBoundsList);
    assertNull(geoWithZoomPoint, "Object returned as non-null for complexity POINT");
  }
}
