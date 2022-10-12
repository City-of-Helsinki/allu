package fi.hel.allu.common.domain.serialization.helsinkixml;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

/**
 * XML Mapping for WFSFeatureCollection consisting of Helsinki city districts.
 */
@XmlRootElement(name = "FeatureCollection", namespace = "http://www.opengis.net/wfs")
public class CityDistrictXml {
  @XmlElement(name = "featureMember", namespace = "http://www.opengis.net/gml")
  public List<FeatureMember> featureMember;

  public static class FeatureMember {
    @XmlElement(name = "Kaupunginosajako", namespace = "https://www.hel.fi/avoindata")
    public HelsinkiKaupunginosajako cityDistrict;

    @Override
    public String toString() {
      return "FeatureMember{" +
          "cityDistrict=" + cityDistrict +
          '}';
    }
  }

  public static class HelsinkiKaupunginosajako {
    @XmlElement(name = "tunnus", namespace = "https://www.hel.fi/avoindata")
    public int districtId;
    @XmlElement(name = "nimi_fi", namespace = "https://www.hel.fi/avoindata")
    public String districtName;
    @XmlElement(name = "geom", namespace = "https://www.hel.fi/avoindata")
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
