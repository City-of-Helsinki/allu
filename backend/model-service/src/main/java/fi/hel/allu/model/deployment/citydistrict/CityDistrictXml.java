package fi.hel.allu.model.deployment.citydistrict;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * XML Mapping for WFSFeatureCollection consisting of Helsinki city districts.
 */
@XmlRootElement(name = "FeatureCollection", namespace = "http://www.opengis.net/wfs")
public class CityDistrictXml {
  @XmlElement(name = "featureMember", namespace = "http://www.opengis.net/gml")
  public List<FeatureMember> featureMember;

  public static class FeatureMember {
    @XmlElement(name = "Kaupunginosajako", namespace = "http://www.hel.fi/hel")
    public HelsinkiKaupunginosajako cityDistrict;

    @Override
    public String toString() {
      return "FeatureMember{" +
          "cityDistrict=" + cityDistrict +
          '}';
    }
  }

  public static class HelsinkiKaupunginosajako {
    @XmlElement(name = "tunnus", namespace = "http://www.hel.fi/hel")
    public int districtId;
    @XmlElement(name = "nimi_fi", namespace = "http://www.hel.fi/hel")
    public String districtName;
    @XmlElement(name = "geom", namespace = "http://www.hel.fi/hel")
    public HelsinkiGeom geometry;

    @Override
    public String toString() {
      return "HelsinkiKaupunginosajako{" +
          "districtId=" + districtId +
          ", districtName='" + districtName + '\'' +
          ", geometry=" + geometry +
          '}';
    }
  }

  public static class HelsinkiGeom {
    @XmlElement(name = "Polygon", namespace = "http://www.opengis.net/gml")
    public GmlPolygon polygon;

    @Override
    public String toString() {
      return "HelsinkiGeom{" +
          "polygon=" + polygon +
          '}';
    }
  }

  public static class GmlPolygon {
    @XmlElement(name = "outerBoundaryIs", namespace = "http://www.opengis.net/gml")
    public GmlOuterBoundary outerBoundary;

    @Override
    public String toString() {
      return "GmlPolygon{" +
          "outerBoundary=" + outerBoundary +
          '}';
    }
  }

  public static class GmlOuterBoundary {
    @XmlElement(name = "LinearRing", namespace = "http://www.opengis.net/gml")
    public GmlLinearRing linearRing;

    @Override
    public String toString() {
      return "GmlOuterBoundary{" +
          "linearRing=" + linearRing +
          '}';
    }
  }

  public static class GmlLinearRing {
    @XmlElement(name = "coordinates", namespace = "http://www.opengis.net/gml")
    public String coordinates;

    @Override
    public String toString() {
      return "GmlLinearRing{" +
          "coordinates='" + coordinates + '\'' +
          '}';
    }
  }

  @Override
  public String toString() {
    return "CityDistrictXml{" +
        "featureMember=" + featureMember +
        '}';
  }
}
