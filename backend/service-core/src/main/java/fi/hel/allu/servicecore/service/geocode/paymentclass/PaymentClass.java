package fi.hel.allu.servicecore.service.geocode.paymentclass;

import fi.hel.allu.servicecore.service.geocode.VariablesUtils;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class PaymentClass {

  @XmlElement(name = "maksuluokka", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public String paymentClass;

  @XmlElement(name = "tietopalvelu_id", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public int dataServiceId;

  @XmlElement(name = "geom", namespace = VariablesUtils.HELSINKI_NAMESPACE, required = false)
  public Geom geom;

  public String getPaymentClass() {
    return paymentClass;
  }

  public List<PolygonCoordinates> getCoordinates() {
    return geom != null ? geom.getCoordinates() : null;
  }

  @Override
  public String toString() {
    return "PaymentClass{" +
      "dataServiceId=" + dataServiceId +
      ", paymentClass='" + paymentClass + '\'' +
      '}';
  }
}
