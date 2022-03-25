package fi.hel.allu.servicecore.service.geocode.featuremember;


import fi.hel.allu.servicecore.service.geocode.paymentlevel.PaymentLevelZone;
import fi.hel.allu.servicecore.service.geocode.VariablesUtils;

import javax.xml.bind.annotation.XmlElement;

public class FeatureZoneMember {

  @XmlElement(name = "Terassit_maksuvyohyke", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public PaymentLevelZone paymentLevelZone;

  @Override
  public String toString() {
    return "FeatureZoneMember{" +
      "paymentZone =" + paymentLevelZone +
      '}';
  }
}
