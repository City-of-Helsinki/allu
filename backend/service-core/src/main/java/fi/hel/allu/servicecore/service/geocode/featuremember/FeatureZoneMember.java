package fi.hel.allu.servicecore.service.geocode.featuremember;


import fi.hel.allu.servicecore.service.geocode.maksu.Maksuvyohyke;
import fi.hel.allu.servicecore.service.geocode.VariablesUtils;

import javax.xml.bind.annotation.XmlElement;

public class FeatureZoneMember {

  @XmlElement(name = "Terassit_maksuvyohyke", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public Maksuvyohyke paymentZone;

  @Override
  public String toString() {
    return "FeatureOldClassMember{" +
      "paymentZone =" + paymentZone +
      '}';
  }
}
