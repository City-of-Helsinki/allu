package fi.hel.allu.servicecore.service.geocode.paymentzone;

import fi.hel.allu.servicecore.service.geocode.VariablesUtils;

import jakarta.xml.bind.annotation.XmlElement;

public class PaymentZone {

  @XmlElement(name = "maksuvyohyke", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public String paymentZone;

  @XmlElement(name = "tietopalvelu_id", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public int dataServiceId;

  public String getPaymentZone() {
    return paymentZone;
  }

  @Override
  public String toString() {
    return "PaymentZone{" +
      "dataServiceId=" + dataServiceId +
      ", paymentZone='" + paymentZone + '\'' +
      '}';
  }
}
