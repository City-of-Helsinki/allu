package fi.hel.allu.model.pricing;

import java.time.ZonedDateTime;

import fi.hel.allu.model.domain.Location;

public class AreaRentalLocationPrice {

  private final Location location;
  private final int numUnits;
  private int dailyPrice;

  public AreaRentalLocationPrice(Location location, double areaUnit) {
    this.location = location;
    this.numUnits = (int)Math.round(Math.ceil(location.getEffectiveArea() / areaUnit));
  }

  public void setDailyPrice(int dailyPrice) {
    this.dailyPrice = dailyPrice;
  }

  public String getPaymentClass() {
    return location.getEffectivePaymentTariff();
  }

  public int getNumUnits() {
    return numUnits;
  }

  public Integer getLocationKey() {
    return location.getLocationKey();
  }

  public double getArea() {
    return location.getEffectiveArea();
  }

  public int getDailyPrice() {
    return dailyPrice;
  }

  public Integer getLocationId() {
    return location.getId();
  }

  public ZonedDateTime getStartTime() {
    return location.getStartTime();
  }

  public ZonedDateTime getEndTime() {
    return location.getEndTime();
  }





}
