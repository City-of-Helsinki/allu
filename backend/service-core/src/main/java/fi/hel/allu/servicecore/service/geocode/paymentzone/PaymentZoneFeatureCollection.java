package fi.hel.allu.servicecore.service.geocode.paymentzone;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * XML Mapping for WFSFeatureCollection consisting of Helsinki payment zones.
 */
@XmlRootElement(name = "FeatureCollection", namespace = "http://www.opengis.net/wfs")
public class PaymentZoneFeatureCollection {

  @XmlElement(name = "featureMember", namespace = "http://www.opengis.net/gml")
  public List<PaymentZoneFeatureMember> featureMembers;


}
