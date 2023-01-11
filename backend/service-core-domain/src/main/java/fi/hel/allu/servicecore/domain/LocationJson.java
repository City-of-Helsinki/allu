package fi.hel.allu.servicecore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;
import fi.hel.allu.common.validator.NotFalse;
import fi.hel.allu.model.domain.LocationInterface;
import io.swagger.v3.oas.annotations.media.Schema;
import org.geolatte.geom.Geometry;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * in Finnish: Hakemuksen sijainti
 */
@Schema(description = "Application location")
@NotFalse(rules = {"startTime, startTimeNotAfterEndTimeValidation, start time must be before end time"})
public class LocationJson implements StartTimeInterface, LocationInterface {
  private Integer id;
  private Integer locationKey;
  private Integer locationVersion;
  @NotNull(message = "{location.startTime}")
  private ZonedDateTime startTime;
  @NotNull(message = "{location.endTime}")
  private ZonedDateTime endTime;
  private String additionalInfo;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;
  private Double area;
  private Double areaOverride;
  @Valid
  private PostalAddressJson postalAddress;
  private String address;
  private List<Integer> fixedLocationIds;
  private Integer cityDistrictId;
  private Integer cityDistrictIdOverride;
  private String paymentTariff;
  private String paymentTariffOverride;
  private Boolean underpass;
  private ZonedDateTime customerStartTime;
  private ZonedDateTime customerEndTime;
  private ZonedDateTime customerReportingTime;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Location key. Each new location for one application gets a key greater than the previous key.", accessMode = Schema.AccessMode.READ_ONLY)
  public Integer getLocationKey() {
    return locationKey;
  }

  public void setLocationKey(Integer locationKey) {
    this.locationKey = locationKey;
  }

  @Schema(description = "Version of the location. If the location is updated, then new version will get higher version number than the previous.", accessMode = Schema.AccessMode.READ_ONLY)
  public Integer getLocationVersion() {
    return locationVersion;
  }

  public void setLocationVersion(Integer locationVersion) {
    this.locationVersion = locationVersion;
  }


  @Schema(description = "The time location use starts", required = true)
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  @Schema(description = "The time location use ends", required = true)
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  @Schema(description = "Additional information for the location")
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  @Schema(description =
      "Location geometry in <a href=\"https://tools.ietf.org/html/rfc7946\">GeoJSON</a> with following limitations:"
      +"<ul>"
      +"<li>Feature / FeatureCollection is currently not supported, geometry should be given as <a href=\"https://tools.ietf.org/html/rfc7946#section-3.1.8\">GeometryCollection</a>.</li>"
     + "<li>Only named CRS is supported, the given name must either be of the form: urn:ogc:def:crs:EPSG:x.y:4326 (x.y: the version of the EPSG) or of the form EPSG:4326</li>"
      +"</ul>")
  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  @Schema(description = "Calculated location area in sq meters", accessMode = Schema.AccessMode.READ_ONLY)
  public Double getArea() {
    return area;
  }

  public void setArea(Double area) {
    this.area = area;
  }

  @Schema(description = "The user overridden area in sq. meters or null, if override is not set")
  public Double getAreaOverride() {
    return areaOverride;
  }

  public void setAreaOverride(Double areaOverride) {
    this.areaOverride = areaOverride;
  }

  @Schema(description = "Address of the location")
  public PostalAddressJson getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressJson postalAddress) {
    this.postalAddress = postalAddress;
  }

  @Schema(description = "Fixed location IDs for this area")
  public List<Integer> getFixedLocationIds() {
    return fixedLocationIds;
  }

  public void setFixedLocationIds(List<Integer> fixedLocationIds) {
    this.fixedLocationIds = fixedLocationIds;
  }

  @Schema(description = "Calculated city district ID for the location", accessMode = Schema.AccessMode.READ_ONLY)
  public Integer getCityDistrictId() {
    return cityDistrictId;
  }

  public void setCityDistrictId(Integer cityDistrictId) {
    this.cityDistrictId = cityDistrictId;
  }

  @Schema(description = "The user overridden city district id")
  public Integer getCityDistrictIdOverride() {
    return cityDistrictIdOverride;
  }

  public void setCityDistrictIdOverride(Integer cityDistrictIdOverride) {
    this.cityDistrictIdOverride = cityDistrictIdOverride;
  }

  @Schema(description = "Calculated payment tariff (maksuluokka) of the location", accessMode = Schema.AccessMode.READ_ONLY)
  public String getPaymentTariff() {
    return paymentTariff;
  }

  public void setPaymentTariff(String paymentTariff) {
    this.paymentTariff = paymentTariff;
  }

  @Schema(description = "User overridden payment tariff (maksuluokka) of the location")
  public String getPaymentTariffOverride() {
    return paymentTariffOverride;
  }

  public void setPaymentTariffOverride(String paymentTariffOverride) {
    this.paymentTariffOverride = paymentTariffOverride;
  }


  @Schema(description = "Underpass (altakuljettava)")
  public Boolean getUnderpass() {
    return underpass;
  }

  public void setUnderpass(Boolean underpass) {
    this.underpass = underpass;
  }

  @JsonIgnore
  public boolean getStartTimeNotAfterEndTimeValidation() {
    if (startTime == null || endTime == null) {
      return true; // this prevents npe in this validator, the null values will trigger separate validations
    }
    return !startTime.isAfter(endTime);
  }

  public String getAddress() {
    return address;
  }

  @Schema(description = "Location address in string or name of the fixed location", accessMode = Schema.AccessMode.READ_ONLY)
  public void setAddress(String address) {
    this.address = address;
  }

  @Schema(description = "Location start time reported by the customer")
  public ZonedDateTime getCustomerStartTime() {
    return customerStartTime;
  }

  public void setCustomerStartTime(ZonedDateTime customerStartTime) {
    this.customerStartTime = customerStartTime;
  }

  @Schema(description = "Location end time reported by the customer")
  public ZonedDateTime getCustomerEndTime() {
    return customerEndTime;
  }

  public void setCustomerEndTime(ZonedDateTime customerEndTime) {
    this.customerEndTime = customerEndTime;
  }

  @Schema(description = "Time when customer reported start and/or end time for the location")
  public ZonedDateTime getCustomerReportingTime() {
    return customerReportingTime;
  }

  public void setCustomerReportingTime(ZonedDateTime customerReportingTime) {
    this.customerReportingTime = customerReportingTime;
  }
}