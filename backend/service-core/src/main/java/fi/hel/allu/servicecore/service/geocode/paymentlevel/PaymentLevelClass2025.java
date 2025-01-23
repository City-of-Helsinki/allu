package fi.hel.allu.servicecore.service.geocode.paymentlevel;

import fi.hel.allu.servicecore.service.geocode.VariablesUtils;
import fi.hel.allu.servicecore.service.geocode.paymentclass.Geom;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class PaymentLevelClass2025 {

  @XmlElement(name = "maksuluokka", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public String payment;

  @XmlElement(name = "tietopalvelu_id", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public int id;

  @XmlElement(name = "geom", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public Geom geom;

  public List<String> getCoordinates() {
    return geom.getCoordinates();
  };

  public PaymentLevelInfo getPaymentLevelInfo() {
    return new PaymentLevelInfo(payment, geom.getCoordinates());
  }

  @Override
  public String toString() {
    return "PaymentLevelClass2025{" +
      "id=" + id +
      ", paymentClass ='" + payment + '\'' +
      '}';
  }
}

