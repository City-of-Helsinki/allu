package fi.hel.allu.servicecore.service.geocode.paymentzone;


import fi.hel.allu.servicecore.service.geocode.paymentzone.PaymentZone;
import fi.hel.allu.servicecore.service.geocode.VariablesUtils;

import javax.xml.bind.annotation.XmlElement;

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
