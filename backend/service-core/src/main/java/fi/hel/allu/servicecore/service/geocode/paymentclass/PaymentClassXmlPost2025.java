package fi.hel.allu.servicecore.service.geocode.paymentclass;


import fi.hel.allu.servicecore.service.geocode.featuremember.FeatureClassMember;
import fi.hel.allu.servicecore.service.geocode.paymentlevel.PaymentLevelClass;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;

@XmlRootElement(name = "FeatureCollection", namespace = "http://www.opengis.net/wfs")
public class PaymentClassXmlPost2025 implements PaymentClassXml {

  @XmlElement(name = "featureMember", namespace = "http://www.opengis.net/gml")
  public List<FeatureClassMember> featureMember;

  @Override
  public List<FeatureClassMember> getFeatureMemeber() {
    return featureMember;
  }

  public HashMap<String, List<PolygonCoordinates>> getPaymentLevels() {
    HashMap<String, List<PolygonCoordinates>> resultMap = new HashMap<String, List<PolygonCoordinates>>();

    if (featureMember == null) return resultMap;

    for (FeatureClassMember member : featureMember) {
      PaymentLevelClass plc = member.paymentLevelClass;
      if (resultMap.containsKey(plc.getPaymentLevelClass()))
        resultMap.get(plc.getPaymentLevelClass()).addAll(plc.getCoordinates());
      else resultMap.put(plc.getPaymentLevelClass(), plc.getCoordinates());
    }

    return resultMap;
  }
}
