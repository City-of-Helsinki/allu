package fi.hel.allu.servicecore.service.geocode.featuremember;

import fi.hel.allu.servicecore.service.geocode.VariablesUtils;
import fi.hel.allu.servicecore.service.geocode.paymentlevel.PaymentLevel;

import javax.xml.bind.annotation.XmlElement;

public class FeatureClassMemberPost2026 {
  @XmlElement(name = "Katutoiden_maksuluokat_2026", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public PaymentLevel paymentLevel;

  public PaymentLevel getPaymentLevel() {
    return paymentLevel;
  }

  @Override
  public String toString() {
    return "FeatureClassMemberPost2026{" +
      "paymentLevel=" + paymentLevel +
      '}';
  }
}
