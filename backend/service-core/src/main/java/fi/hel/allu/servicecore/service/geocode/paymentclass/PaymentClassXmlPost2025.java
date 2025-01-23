package fi.hel.allu.servicecore.service.geocode.paymentclass;


import fi.hel.allu.servicecore.service.geocode.featuremember.FeatureClassMember;
import fi.hel.allu.servicecore.service.geocode.featuremember.FeatureClassMemberPost2025;
import fi.hel.allu.servicecore.service.geocode.paymentlevel.PaymentLevelInfo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "FeatureCollection", namespace = "http://www.opengis.net/wfs")
public class PaymentClassXmlPost2025 {

  @XmlElement(name = "featureMember", namespace = "http://www.opengis.net/gml")
  public List<FeatureClassMemberPost2025> featureMember;

  public HashMap<String, List<String>> getPaymentLevels() {
    HashMap<String, List<String>> resultMap = new HashMap<String, List<String>>();

    for (FeatureClassMemberPost2025 member : featureMember) {
      PaymentLevelInfo info = member.getPaymentLevelInfo();
      if (resultMap.containsKey(info.getPaymentLevel()))
        resultMap.get(info.getPaymentLevel()).addAll(info.getCoordinates());
      else resultMap.put(info.getPaymentLevel(), info.getCoordinates());
    }

    return resultMap;
  }
}
