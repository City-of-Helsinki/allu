package fi.hel.allu.servicecore.service.geocode.paymentlevel;

import fi.hel.allu.servicecore.service.geocode.VariablesUtils;

import javax.xml.bind.annotation.XmlElement;

/**
 * Represents a payment zone (maksuvyohyke) with its identifier.
 * Used for WFS response unmarshalling of payment zone data.
 * 
 * Note: dataServiceId is an optional field as it may not be present in all WFS response versions.
 */
public class PaymentZone {

  @XmlElement(name = "maksuvyohyke", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public String paymentZone;

  @XmlElement(name = "tietopalvelu_id", namespace = VariablesUtils.HELSINKI_NAMESPACE, required = false)
  public Integer dataServiceId;

  public String getPaymentZone() {
    return paymentZone;
  }

  /**
   * For compatibility with older code using getPayment()
   */
  public String getPayment() {
    return paymentZone;
  }

  @Override
  public String toString() {
    return "PaymentZone{" +
      "dataServiceId=" + (dataServiceId != null ? dataServiceId : "null") +
      ", paymentZone='" + paymentZone + '\'' +
      '}';
  }
}
