package fi.hel.allu.servicecore.service.geocode.featuremember;

import fi.hel.allu.servicecore.service.geocode.paymentlevel.PaymentLevel;
import fi.hel.allu.servicecore.service.geocode.VariablesUtils;

import javax.xml.bind.annotation.XmlElement;

public class FeatureClassMemberPost2022 implements FeatureClassMember {

  @XmlElement(name = "Katutoiden_maksuluokat_2022", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public PaymentLevel paymentLevel;

  @Override
  public PaymentLevel getMaksuluokka() {
    return paymentLevel;
  }

  @Override
  public String toString() {
    return "FeatureClassMemberPost2022{" +
      "paymentLevel=" + paymentLevel +
      '}';
  }
}
