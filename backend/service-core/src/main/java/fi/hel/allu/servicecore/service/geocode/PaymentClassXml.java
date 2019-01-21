package fi.hel.allu.servicecore.service.geocode;

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
    @XmlElement(name = "Katutoiden_maksuluokat", namespace = HELSINKI_NAMESPACE)
    public HelsinkiAlluMaksuvyohyke paymentClass;

    @Override
    public String toString() {
      return "FeatureMember{" +
          "paymentClass=" + paymentClass +
          '}';
    }
  }

  public static class HelsinkiAlluMaksuvyohyke {
    @XmlElement(name = "tietopalvelu_id", namespace = HELSINKI_NAMESPACE)
    public int id;
    @XmlElement(name = "maksuluokka", namespace = HELSINKI_NAMESPACE)
    public String paymentClass;

    public String getPaymentClass() {
      return paymentClass;
    }

    @Override
    public String toString() {
      return "HelsinkiAlluMaksuvuohyke{" +
          "id=" + id +
          ", paymentClass='" + paymentClass + '\'' +
          '}';
    }
  }
}
