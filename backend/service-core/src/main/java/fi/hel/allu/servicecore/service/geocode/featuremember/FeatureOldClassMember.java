package fi.hel.allu.servicecore.service.geocode.featuremember;

import fi.hel.allu.servicecore.service.geocode.paymentlevel.PaymentLevelClass;
import fi.hel.allu.servicecore.service.geocode.VariablesUtils;

import javax.xml.bind.annotation.XmlElement;

public class FeatureOldClassMember implements FeatureClassMember {

  @XmlElement(name = "Katutoiden_maksuluokat", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public PaymentLevelClass paymentLevelClass;

  @Override
  public PaymentLevelClass getMaksuluokka() {
    return paymentLevelClass;
  }

  @Override
  public String toString() {
    return "FeatureOldClassMember{" +
      "paymentClass=" + paymentLevelClass +
      '}';
  }
}
