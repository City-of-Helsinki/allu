package fi.hel.allu.servicecore.service.geocode.featuremember;

import fi.hel.allu.servicecore.service.geocode.paymentlevel.PaymentZone;
import fi.hel.allu.servicecore.service.geocode.VariablesUtils;

import javax.xml.bind.annotation.XmlElement;

public class FeatureZoneMember {

  @XmlElement(name = "Terassit_maksuvyohyke", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public PaymentZone paymentLevelZone;

  @Override
  public String toString() {
    return "FeatureZoneMember{" +
      "paymentLevelZone=" + paymentLevelZone +
      '}';
  }
}
