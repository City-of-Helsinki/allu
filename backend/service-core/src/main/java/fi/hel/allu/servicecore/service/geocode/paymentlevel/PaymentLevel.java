package fi.hel.allu.servicecore.service.geocode.paymentlevel;

import fi.hel.allu.servicecore.service.geocode.VariablesUtils;
import fi.hel.allu.servicecore.service.geocode.paymentclass.Geom;
import fi.hel.allu.servicecore.service.geocode.paymentclass.PolygonCoordinates;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Represents a payment level (maksuluokka) with its class and geographic information.
 * Used for WFS response unmarshalling and payment level data access across all tariff versions.
 * Supports both simple payment class representation (pre-2025) and complex geometry representation (post-2025).
 * 
 * Note: dataServiceId and geom are optional fields as they may not be present in all WFS response versions:
 * - Pre-2022/2022 versions: no tietopalvelu_id or geom
 * - Post-2025 versions: have both tietopalvelu_id and geom
 */
public class PaymentLevel {

  @XmlElement(name = "maksuluokka", namespace = VariablesUtils.HELSINKI_NAMESPACE)
  public String paymentClass;

  @XmlElement(name = "tietopalvelu_id", namespace = VariablesUtils.HELSINKI_NAMESPACE, required = false)
  public Integer dataServiceId;

  @XmlElement(name = "geom", namespace = VariablesUtils.HELSINKI_NAMESPACE, required = false)
  public Geom geom;

  public List<PolygonCoordinates> getCoordinates() {
    return geom != null ? geom.getCoordinates() : null;
  }

  public String getPaymentLevel() {
    return paymentClass;
  }

  /**
   * For compatibility with older code using getPayment()
   */
  public String getPayment() {
    return paymentClass;
  }

  @Override
  public String toString() {
    return "PaymentLevel{" +
      "dataServiceId=" + (dataServiceId != null ? dataServiceId : "null") +
      ", paymentClass='" + paymentClass + '\'' +
      '}';
  }
}

