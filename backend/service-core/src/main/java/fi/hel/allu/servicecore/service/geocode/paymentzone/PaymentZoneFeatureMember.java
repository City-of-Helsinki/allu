package fi.hel.allu.servicecore.service.geocode.paymentzone;


import fi.hel.allu.servicecore.service.geocode.VariablesUtils;

import jakarta.xml.bind.annotation.XmlElement;

public class PaymentZoneFeatureMember {

  @XmlElement(name = "Terassit_maksuvyohyke", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public PaymentZone paymentZone;

  @Override
  public String toString() {
    return "PaymentZoneFeatureMember{" +
      "paymentZone =" + paymentZone +
      '}';
  }
}
