package fi.hel.allu.servicecore.service.geocode.featuremember;

import fi.hel.allu.servicecore.service.geocode.paymentlevel.PaymentLevelClass;
import fi.hel.allu.servicecore.service.geocode.VariablesUtils;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class FeatureClassMember {

  @XmlElements({
    @XmlElement(name = "Katutoiden_maksuluokat", namespace = VariablesUtils.HELSINKI_NAMESPACE),
    @XmlElement(name = "Katutoiden_maksuluokat_2022", namespace = VariablesUtils.HELSINKI_NAMESPACE),
    @XmlElement(name = "Katutoiden_maksuluokat_2025", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  })
  public PaymentLevelClass paymentLevelClass;

  public PaymentLevelClass getPaymentLevelClass() {
    return paymentLevelClass;
  }

  @Override
  public String toString() {
    return "FeatureClassMember{" +
      "paymentLevelClass=" + paymentLevelClass +
      '}';
  }
}
