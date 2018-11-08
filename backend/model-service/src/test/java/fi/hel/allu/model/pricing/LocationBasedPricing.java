package fi.hel.allu.model.pricing;

import fi.hel.allu.model.domain.Location;

import java.time.ZonedDateTime;

public abstract class LocationBasedPricing {

  Location getLocation(int key, double area, String paymentClass, ZonedDateTime start, ZonedDateTime end) {
    final Location location = new Location();
    location.setLocationKey(key);
    location.setArea(area);
    location.setPaymentTariff(paymentClass);
    location.setStartTime(start);
    location.setEndTime(end);
    return location;
  }
}
