package fi.hel.allu.model.deployment.paymentclass;

import fi.hel.allu.model.deployment.helsinkixml.HelsinkiGeom;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

/**
 * XML Mapping for WFSFeatureCollection consisting of Helsinki payment classes.
 */
@XmlRootElement(name = "FeatureCollection", namespace = "http://www.opengis.net/wfs")
public class PaymentClassXml {
  static final String HELSINKI_NAMESPACE = "https://www.hel.fi/hel";

  @XmlElement(name = "featureMember", namespace = "http://www.opengis.net/gml")
  public List<FeatureMember> featureMember;

  public static class FeatureMember {
    @XmlElement(name = "Allu_maksuvyohykkeet_testi", namespace = HELSINKI_NAMESPACE)
    public HelsinkiAlluMaksuvyohyke paymentClass;

    @Override
    public String toString() {
      return "FeatureMember{" +
          "paymentClass=" + paymentClass +
          '}';
    }
  }

  public static class HelsinkiAlluMaksuvyohyke {
    @XmlElement(name = "id", namespace = HELSINKI_NAMESPACE)
    public int id;
    @XmlElement(name = "maksuvyohyke", namespace = HELSINKI_NAMESPACE)
    public String paymentClass;
    @XmlElement(name = "muokattu", namespace = HELSINKI_NAMESPACE)
    public String modificationDate;
    @XmlElement(name = "geom", namespace = HELSINKI_NAMESPACE)
    public HelsinkiGeom geometry;

    @Override
    public String toString() {
      return "HelsinkiAlluMaksuvuohyke{" +
          "id=" + id +
          ", paymentClass='" + paymentClass + '\'' +
          ", modificationDate='" + modificationDate + '\'' +
          ", geometry=" + geometry +
          '}';
    }
  }

}
