package fi.hel.allu.servicecore.service.geocode.paymentlevel;

import fi.hel.allu.servicecore.service.geocode.VariablesUtils;

import javax.xml.bind.annotation.XmlElement;

public class PaymentLevelZone extends AbstractPaymentLevel {

  @XmlElement(name = "maksuvyohyke", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public String payment;

  public String getPayment() {
    return payment;
  }


  @Override
  public String toString() {
    return "HelsinkiAlluMaksuvuohyke{" +
      "id=" + id +
      ", paymentZone ='" + payment + '\'' +
      '}';
  }
}
