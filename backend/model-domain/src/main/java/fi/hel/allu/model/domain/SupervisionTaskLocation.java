package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

import org.geolatte.geom.Geometry;

public class SupervisionTaskLocation extends AbstractLocation {

  private Integer applicationLocationId;

  public SupervisionTaskLocation() {
  }

  public SupervisionTaskLocation(Integer locationKey, Geometry geometry, ZonedDateTime startTime, ZonedDateTime endTime,
      String paymentTariff, Boolean underpass, Integer applicationLocationId) {
    super(locationKey, geometry, startTime, endTime, paymentTariff, underpass);
    this.applicationLocationId = applicationLocationId;
  }

  public static SupervisionTaskLocation fromApplicationLocation(Location location) {
    return new SupervisionTaskLocation(location.getLocationKey(), location.getGeometry(), location.getStartTime(),
        location.getEndTime(), location.getEffectivePaymentTariff(), location.getUnderpass(), location.getId());
  }

  public Integer getApplicationLocationId() {
    return applicationLocationId;
  }

  public void setApplicationLocationId(Integer applicationLocationId) {
    this.applicationLocationId = applicationLocationId;
  }
}
