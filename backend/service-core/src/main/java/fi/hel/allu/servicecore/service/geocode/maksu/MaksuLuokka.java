package fi.hel.allu.servicecore.service.geocode.maksu;

import fi.hel.allu.servicecore.service.geocode.VariablesUtils;

import javax.xml.bind.annotation.XmlElement;

public class MaksuLuokka extends HelsinkiAlluMaksu {

  @XmlElement(name = "maksuluokka", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public String payment;

  public String getPayment() {
    return payment;
  }


  @Override
  public String toString() {
    return "HelsinkiAlluMaksuvuohyke{" +
      "id=" + id +
      ", paymentClas ='" + payment + '\'' +
      '}';
  }
}
