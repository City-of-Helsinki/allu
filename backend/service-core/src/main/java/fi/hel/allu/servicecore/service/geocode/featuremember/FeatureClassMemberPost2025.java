package fi.hel.allu.servicecore.service.geocode.featuremember;

import fi.hel.allu.servicecore.service.geocode.VariablesUtils;
import fi.hel.allu.servicecore.service.geocode.paymentlevel.PaymentLevelClass;

import javax.xml.bind.annotation.XmlElement;

public class FeatureClassMemberPost2025 {
  @XmlElement(name = "Katutoiden_maksuluokat_2025", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public PaymentLevelClass paymentLevelClass;

  public PaymentLevelClass getPaymentLevelClass() {
    return paymentLevelClass;
  }

  @Override
  public String toString() {
    return "FeatureClassMemberPost2025{" +
      "paymentLevelClass=" + paymentLevelClass +
      '}';
  }
}
