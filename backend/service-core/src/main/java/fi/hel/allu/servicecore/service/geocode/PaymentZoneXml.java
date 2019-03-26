package fi.hel.allu.servicecore.service.geocode;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

/**
 * XML Mapping for WFSFeatureCollection consisting of Helsinki payment classes.
 */
@XmlRootElement(name = "FeatureCollection", namespace = "http://www.opengis.net/wfs")
public class PaymentZoneXml {
  static final String HELSINKI_NAMESPACE = "https://www.hel.fi/hel";

  @XmlElement(name = "featureMember", namespace = "http://www.opengis.net/gml")
  public List<FeatureMember> featureMember;

  public static class FeatureMember {
    @XmlElement(name = "Terassit_maksuvyohyke", namespace = HELSINKI_NAMESPACE)
    public HelsinkiAlluMaksuvyohyke paymentZone;

    @Override
    public String toString() {
      return "FeatureMember{" +
          "paymentZone =" + paymentZone +
          '}';
    }
  }

  public static class HelsinkiAlluMaksuvyohyke {
    @XmlElement(name = "tietopalvelu_id", namespace = HELSINKI_NAMESPACE)
    public int id;
    @XmlElement(name = "maksuvyohyke", namespace = HELSINKI_NAMESPACE)
    public String paymentZone;

    public String getPaymentZone() {
      return paymentZone;
    }

    @Override
    public String toString() {
      return "HelsinkiAlluMaksuvuohyke{" +
          "id=" + id +
          ", paymentZone ='" + paymentZone + '\'' +
          '}';
    }
  }
}
