package fi.hel.allu.model.pricing;

import fi.hel.allu.common.domain.types.SurfaceHardness;

public class OutdoorPricingConfiguration {

  private long baseCharge;
  private int buildDiscountPercent;
  private Integer fixedLocationId;
  private Integer zoneId;
  private SurfaceHardness surfaceHardness;

  public OutdoorPricingConfiguration() {
    // for beans
  }

  public OutdoorPricingConfiguration(long baseCharge, int buildDiscountPercent) {
    super();
    this.baseCharge = baseCharge;
    this.buildDiscountPercent = buildDiscountPercent;
  }

  /**
   * Get the base charge
   *
   * @return the base charge in 1/100 eurocents (10000L ~ 1 EUR)
   */
  public long getBaseCharge() {
    return baseCharge;
  }

  public void setBaseCharge(long baseCharge) {
    this.baseCharge = baseCharge;
  }

  /**
   * Get the discount percent for build/teardown days
   *
   * @return the buildDiscountPercent
   */
  public int getBuildDiscountPercent() {
    return buildDiscountPercent;
  }

  public void setBuildDiscountPercent(int buildDiscountPercent) {
    this.buildDiscountPercent = buildDiscountPercent;
  }

  public Integer getFixedLocationId() {
    return fixedLocationId;
  }

  public void setFixedLocationId(Integer fixedLocationId) {
    this.fixedLocationId = fixedLocationId;
  }

  public Integer getZoneId() {
    return zoneId;
  }

  public void setZoneId(Integer zoneId) {
    this.zoneId = zoneId;
  }

  public SurfaceHardness getSurfaceHardness() {
    return surfaceHardness;
  }

  public void setSurfaceHardness(SurfaceHardness surfaceHardness) {
    this.surfaceHardness = surfaceHardness;
  }

}
