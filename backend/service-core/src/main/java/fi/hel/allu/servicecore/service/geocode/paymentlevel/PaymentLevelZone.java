package fi.hel.allu.servicecore.service.geocode.paymentlevel;

import fi.hel.allu.servicecore.service.geocode.VariablesUtils;

import javax.xml.bind.annotation.XmlElement;

public class PaymentLevelZone {

  @XmlElement(name = "maksuvyohyke", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public String paymentLevelZone;

  @XmlElement(name = "tietopalvelu_id", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public int dataServiceId;

  public String getPaymentLevelZone() {
    return paymentLevelZone;
  }

  @Override
  public String toString() {
    return "PaymentLevelZone{" +
      "dataServiceId=" + dataServiceId +
      ", paymentLevelZone='" + paymentLevelZone + '\'' +
      '}';
  }
}
