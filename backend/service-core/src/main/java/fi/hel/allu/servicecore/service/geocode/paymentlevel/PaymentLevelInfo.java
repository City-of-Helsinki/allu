package fi.hel.allu.servicecore.service.geocode.paymentlevel;

import java.util.List;

public class PaymentLevelInfo {
  public String paymentLevel;
  public List<String> coordinates;

  public PaymentLevelInfo(String paymentLevel, List<String> coordinates) {
    this.paymentLevel = paymentLevel;
    this.coordinates = coordinates;
  }

  public String getPaymentLevel() {
    return paymentLevel;
  }

  public List<String> getCoordinates() {
    return coordinates;
  }
}
