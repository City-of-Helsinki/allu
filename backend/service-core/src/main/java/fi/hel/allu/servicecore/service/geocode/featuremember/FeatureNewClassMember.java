package fi.hel.allu.servicecore.service.geocode.featuremember;

import fi.hel.allu.servicecore.service.geocode.maksu.MaksuLuokka;
import fi.hel.allu.servicecore.service.geocode.VariablesUtils;

import javax.xml.bind.annotation.XmlElement;

public class FeatureNewClassMember implements FeatureClassMember {

  @XmlElement(name = "Katutoiden_maksuluokat_2022", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public MaksuLuokka paymentClass;

  @Override
  public MaksuLuokka getMaksuluokka() {
    return paymentClass;
  }

  @Override
  public String toString() {
    return "FeatureOldClassMember{" +
      "paymentClass=" + paymentClass +
      '}';
  }
}
