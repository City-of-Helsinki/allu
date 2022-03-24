package fi.hel.allu.servicecore.service.geocode.maksu;

import fi.hel.allu.servicecore.service.geocode.VariablesUtils;

import javax.xml.bind.annotation.XmlElement;

public abstract class HelsinkiAlluMaksu {

  @XmlElement(name = "tietopalvelu_id", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public int id;


  @Override
  public String toString() {
    return "HelsinkiAlluMaksuvuohyke{" +
      "Error String coming from Abstract Class: HelsinkiAlluMaksu" + '\'' +
      '}';
  }
}
