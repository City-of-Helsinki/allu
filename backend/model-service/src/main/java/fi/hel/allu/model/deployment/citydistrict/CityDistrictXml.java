package fi.hel.allu.model.deployment.citydistrict;

import fi.hel.allu.model.deployment.helsinkixml.HelsinkiGeom;

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

  @Override
  public String toString() {
    return "CityDistrictXml{" +
        "featureMember=" + featureMember +
        '}';
  }
}
