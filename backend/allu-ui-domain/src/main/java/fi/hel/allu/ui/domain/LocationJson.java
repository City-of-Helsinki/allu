package fi.hel.allu.ui.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.model.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.model.domain.serialization.GeometrySerializerProxy;

import org.geolatte.geom.Geometry;

import javax.validation.Valid;

import java.util.List;

/**
 * inFinnish: Hakemuksen sijainti
 */
public class LocationJson {
  private Integer id;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;
  private Double area;
  private Double areaOverride;
  @Valid
  private PostalAddressJson postalAddress;
  private List<Integer> fixedLocationIds;
  private Integer districtId;
  private Integer districtIdOverride;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  /**
   * @return the area in sq. meters
   */
  public Double getArea() {
    return area;
  }

  public void setArea(Double area) {
    this.area = area;
  }

  /**
   * @return the area override in sq. meters or null, if override is not set
   */
  public Double getAreaOverride() {
    return areaOverride;
  }

  public void setAreaOverride(Double areaOverride) {
    this.areaOverride = areaOverride;
  }

  public PostalAddressJson getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressJson postalAddress) {
    this.postalAddress = postalAddress;
  }

  /**
   * Get the location's area-section address ID.
   *
   * @return the fixedLocationId
   */
  public List<Integer> getFixedLocationIds() {
    return fixedLocationIds;
  }

  public void setFixedLocationIds(List<Integer> fixedLocationIds) {
    this.fixedLocationIds = fixedLocationIds;
  }

  /**
   * Get the location's calculated district ID.
   *
   * @return district ID or null
   */
  public Integer getDistrictId() {
    return districtId;
  }

  public void setDistrictId(Integer districtId) {
    this.districtId = districtId;
  }

  /**
   * Get the locations user-overridden district ID
   *
   * @return district ID or null
   */
  public Integer getDistrictIdOverride() {
    return districtIdOverride;
  }

  public void setDistrictIdOverride(Integer districtIdOverride) {
    this.districtIdOverride = districtIdOverride;
  }

}
