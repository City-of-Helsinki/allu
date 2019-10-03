package fi.hel.allu.model.dao;

import java.util.Collection;
import java.util.List;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryCollection;

public class GeometryUtil {

  public static GeometryCollection toGeometryCollection(List<Geometry> geometries) {
    Geometry[] geoArray = geometries.toArray(new Geometry[geometries.size()]);
    return new GeometryCollection(geoArray);
  }

  /*
   * flatten the geometries into destination
   */
  public static void flatten(Geometry geometry, Collection<Geometry> destination) {
    if (geometry instanceof GeometryCollection) {
      ((GeometryCollection) geometry).forEach(g -> flatten(g, destination));
    } else {
      destination.add(geometry);
    }
  }


}
