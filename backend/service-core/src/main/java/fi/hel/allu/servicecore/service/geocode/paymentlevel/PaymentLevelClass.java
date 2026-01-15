package fi.hel.allu.servicecore.service.geocode.paymentlevel;

import fi.hel.allu.servicecore.service.geocode.VariablesUtils;
import fi.hel.allu.servicecore.service.geocode.paymentclass.Geom;
import fi.hel.allu.servicecore.service.geocode.paymentclass.PolygonCoordinates;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class PaymentLevelClass {

  @XmlElement(name = "maksuluokka", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public String paymentLevelClass;

  @XmlElement(name = "tietopalvelu_id", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public int dataServiceId;

  @XmlElement(name = "geom", namespace = VariablesUtils.HELSINKI_NAMESPACE, required = false)
  public Geom geom;

  public String getPaymentLevelClass() {
    return paymentLevelClass;
  }

  public List<PolygonCoordinates> getCoordinates() {
    return geom != null ? geom.getCoordinates() : null;
  }

  @Override
  public String toString() {
    return "PaymentLevelClass{" +
      "dataServiceId=" + dataServiceId +
      ", paymentLevelClass='" + paymentLevelClass + '\'' +
      '}';
  }
}
