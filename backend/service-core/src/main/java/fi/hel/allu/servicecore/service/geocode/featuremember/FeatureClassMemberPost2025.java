package fi.hel.allu.servicecore.service.geocode.featuremember;

import fi.hel.allu.servicecore.service.geocode.VariablesUtils;
import fi.hel.allu.servicecore.service.geocode.paymentlevel.PaymentLevelClass2025;
import fi.hel.allu.servicecore.service.geocode.paymentlevel.PaymentLevelInfo;

import javax.xml.bind.annotation.XmlElement;

public class FeatureClassMemberPost2025 {
  @XmlElement(name = "Katutoiden_maksuluokat_2025", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public PaymentLevelClass2025 paymentLevelClass;

  public PaymentLevelInfo getPaymentLevelInfo() {
    return paymentLevelClass.getPaymentLevelInfo();
  }

  @Override
  public String toString() {
    return "FeatureClassMemberPost2025{" +
      "paymentClass=" + paymentLevelClass +
      '}';
  }
}
