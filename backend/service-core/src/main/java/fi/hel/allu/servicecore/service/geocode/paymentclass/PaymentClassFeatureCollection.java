package fi.hel.allu.servicecore.service.geocode.paymentclass;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;

/**
 * XML Mapping for WFSFeatureCollection consisting of Helsinki payment classes.
 * Unified implementation supporting all payment class versions (pre-2022, post-2022, post-2025).
 */
@XmlRootElement(name = "FeatureCollection", namespace = "http://www.opengis.net/wfs")
public class PaymentClassFeatureCollection {

  @XmlElement(name = "featureMember", namespace = "http://www.opengis.net/gml")
  public List<PaymentClassFeatureMember> featureMembers;

  public List<PaymentClassFeatureMember> getFeatureMembers() {
    return featureMembers;
  }

   public HashMap<String, List<PolygonCoordinates>> getPaymentLevels() {
    HashMap<String, List<PolygonCoordinates>> resultMap = new HashMap<String, List<PolygonCoordinates>>();

    if (featureMembers == null) return resultMap;

    for (PaymentClassFeatureMember member : featureMembers) {
      PaymentClass plc = member.paymentClass;
      if (resultMap.containsKey(plc.getPaymentClass()))
        resultMap.get(plc.getPaymentClass()).addAll(plc.getCoordinates());
      else resultMap.put(plc.getPaymentClass(), plc.getCoordinates());
    }

    return resultMap;
  }
}
