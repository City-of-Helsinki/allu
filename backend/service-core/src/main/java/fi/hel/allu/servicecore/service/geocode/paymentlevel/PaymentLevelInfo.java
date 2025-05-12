package fi.hel.allu.servicecore.service.geocode.paymentlevel;

import fi.hel.allu.servicecore.service.geocode.paymentclass.PolygonCoordinates;

import java.util.List;

public class PaymentLevelInfo {
  public String paymentLevel;
  public List<PolygonCoordinates> coordinates;

  public PaymentLevelInfo(String paymentLevel, List<PolygonCoordinates> coordinates) {
    this.paymentLevel = paymentLevel;
    this.coordinates = coordinates;
  }

  public String getPaymentLevel() {
    return paymentLevel;
  }

  public List<PolygonCoordinates> getCoordinates() {
    return coordinates;
  }
}
