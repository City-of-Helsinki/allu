package fi.hel.allu.common.domain.serialization.helsinkixml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

/**
 * Representation of the helsinki:geom WFS node
 */
@XmlRootElement(name = "geom", namespace = "http://www.hel.fi/hel")
public class HelsinkiGeom {
  public static final String GML_NAMESPACE = "http://www.opengis.net/gml";

  @XmlElement(name = "Polygon", namespace = GML_NAMESPACE)
  public GmlPolygon polygon;

  @Override
  public String toString() {
    return "HelsinkiGeom{" + "polygon=" + polygon + '}';
  }

  public static class GmlPolygon {
    @XmlElement(name = "outerBoundaryIs", namespace = GML_NAMESPACE)
    public GmlBoundary outerBoundary;

    @XmlElement(name = "innerBoundaryIs", namespace = GML_NAMESPACE)
    public List<GmlBoundary> innerBoundary;

    @Override
    public String toString() {
      return "GmlPolygon{" + "outerBoundary=" + outerBoundary + ",innerBoundary=" + innerBoundary + '}';
    }
  }

  public static class GmlBoundary {
    @XmlElement(name = "LinearRing", namespace = GML_NAMESPACE)
    public GmlLinearRing linearRing;

    @Override
    public String toString() {
      return "GmlBoundary{" + "linearRing=" + linearRing + '}';
    }
  }

  public static class GmlLinearRing {
    @XmlElement(name = "coordinates", namespace = GML_NAMESPACE)
    public String coordinates;

    @Override
    public String toString() {
      return "GmlLinearRing{" + "coordinates='" + coordinates + '\'' + '}';
    }
  }

}
