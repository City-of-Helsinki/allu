package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;

public abstract class AbstractLocation {

  private Integer id;
  private Integer locationKey;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private String paymentTariff;
  private Boolean underpass;
  private ZonedDateTime customerStartTime;
  private ZonedDateTime customerEndTime;
  private ZonedDateTime customerReportingTime;


  protected AbstractLocation() {
  }

  protected AbstractLocation(Integer locationKey, Geometry geometry, ZonedDateTime startTime, ZonedDateTime endTime,
      String paymentTariff, Boolean underpass, ZonedDateTime customerStartTime, ZonedDateTime customerEndTime,
      ZonedDateTime customerReportingTime) {
    this.locationKey = locationKey;
    this.geometry = geometry;
    this.startTime = startTime;
    this.endTime = endTime;
    this.paymentTariff = paymentTariff;
    this.underpass = underpass;
    this.customerStartTime = customerStartTime;
    this.customerEndTime = customerEndTime;
    this.customerReportingTime = customerReportingTime;
  }

  /**
   * Get location's database ID
   *
   * @return the ID
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Returns the human readable name of location (actually a number, which can be converted into a name). Each new location for one
   * application gets a key greater than the previous key. In case there are locations 1,2 and 3 and 2 is deleted and a new location is
   * added, the new location gets key 4.
   *
   * @return  Returns the human readable name of location (actually a number, which can be converted into a name).
   */
  public Integer getLocationKey() {
    return locationKey;
  }

  public void setLocationKey(Integer locationKey) {
    this.locationKey = locationKey;
  }

  /**
   * Get the geometry for the location
   *
   * @return the geometry
   */
  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  /**
   * Returns the time location use starts.
   *
   * @return  the time location use starts.
   */
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  /**
   * Returns the time location use ends.
   *
   * @return  the time location use ends.
   */
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  /**
   * Returns the calculated payment tariff (maksuluokka) of the location.
   *
   * @return the calculated payment tariff (maksuluokka) of the location or
   *         <code>null</code>.
   */
  public String getPaymentTariff() {
    return paymentTariff;
  }

  public void setPaymentTariff(String paymentTariff) {
    this.paymentTariff = paymentTariff;
  }

  /**
   * Returns true if it's possible to pass through the reserved area without obstacles (altakuljettava).
   *
   * @return  true if it's possible to pass through the reserved area without obstacles (altakuljettava).
   */
  public Boolean getUnderpass() {
    return underpass;
  }

  public void setUnderpass(Boolean underpass) {
    this.underpass = underpass;
  }

  public ZonedDateTime getCustomerStartTime() {
    return customerStartTime;
  }

  public void setCustomerStartTime(ZonedDateTime customerStartTime) {
    this.customerStartTime = customerStartTime;
  }

  public ZonedDateTime getCustomerEndTime() {
    return customerEndTime;
  }

  public void setCustomerEndTime(ZonedDateTime customerEndTime) {
    this.customerEndTime = customerEndTime;
  }

  public ZonedDateTime getCustomerReportingTime() {
    return customerReportingTime;
  }

  public void setCustomerReportingTime(ZonedDateTime customerReportingTime) {
    this.customerReportingTime = customerReportingTime;
  }

  public boolean equalContent(AbstractLocation other) {
    if (this.startTime == null || !this.startTime.isEqual(other.getStartTime()))
      return false;
    if (this.endTime == null || !this.endTime.isEqual(other.getEndTime()))
      return false;
    if (this.paymentTariff == null || !this.paymentTariff.equals(other.getPaymentTariff()))
      return false;
    if (this.underpass == null || !this.underpass.equals(other.getUnderpass()))
      return false;
    if (this.customerStartTime == null || !this.customerStartTime.isEqual(other.getCustomerStartTime()))
      return false;
    if (this.customerEndTime == null || !this.customerEndTime.isEqual(other.getCustomerEndTime()))
      return false;
    return this.customerReportingTime != null && this.customerReportingTime.isEqual(other.getCustomerReportingTime());
  }

  public boolean equalGeometry(Geometry other) {
    if (this.geometry == null)
      return false;
    return this.geometry.equals(other);
  }
}
