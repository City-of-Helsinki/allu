package fi.hel.allu.model.pricing;

import java.util.Arrays;

public class PricingConfiguration {


  private long baseCharge;
  private int buildDiscountPercent;
  private int durationDiscountPercent;
  private int durationDiscountLimit;
  private Long[] structureExtraCharges;
  private Double[] structureExtraChargeLimits;
  private Long[] areaExtraCharges;
  private Double[] areaExtraChargeLimits;
  private Integer fixedLocationId;

  public PricingConfiguration() {
    // for beans
  }

  public PricingConfiguration(long baseCharge, int buildDiscountPercent, int durationDiscountPercent, int durationDiscountLimit,
      long[] structureExtraCharges, double[] structureExtraChargeLimits, long[] areaExtraCharges, double[] areaExtraChargeLimits) {
    super();
    this.baseCharge = baseCharge;
    this.buildDiscountPercent = buildDiscountPercent;
    this.durationDiscountPercent = durationDiscountPercent;
    this.durationDiscountLimit = durationDiscountLimit;
    this.structureExtraCharges = structureExtraCharges != null
        ? Arrays.stream(structureExtraCharges).boxed().toArray(Long[]::new) : null;
    this.structureExtraChargeLimits = structureExtraChargeLimits != null
        ? Arrays.stream(structureExtraChargeLimits).boxed().toArray(Double[]::new) : null;
    this.areaExtraCharges = areaExtraCharges != null ? Arrays.stream(areaExtraCharges).boxed().toArray(Long[]::new)
        : null;
    this.areaExtraChargeLimits = areaExtraChargeLimits != null
        ? Arrays.stream(areaExtraChargeLimits).boxed().toArray(Double[]::new)
        : null;
    validateAreaExtraCharges();
    validateStructureExtraCharges();
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

  /**
   * Get the discount percent for long time events
   *
   * @return the durationDiscountPercent
   */
  public int getDurationDiscountPercent() {
    return durationDiscountPercent;
  }

  public void setDurationDiscountPercent(int durationDiscountPercent) {
    this.durationDiscountPercent = durationDiscountPercent;
  }

  /**
   * Get the limit (in days) after which the duration discount should be applied
   * (i.e., the number of days that are charged with full price)
   *
   * @return the durationDiscountLimit
   */
  public int getDurationDiscountLimit() {
    return durationDiscountLimit;
  }

  public void setDurationDiscountLimit(int durationDiscountLimit) {
    this.durationDiscountLimit = durationDiscountLimit;
  }

  /**
   * Get the array of structure charges for the event type. Every structure
   * charge is associated with an area limit (@see structureExtraChargeLimits)
   * that specifies the minimum structure are for which the charge is
   * applicable. The unit of the charge is 1/100 eurocents per starting 10 sq.m.
   * of structures.
   *
   * @return the extra charge in 1/100 eurocents.
   */
  public Long[] getStructureExtraCharges() {
    return structureExtraCharges;
  }

  public void setStructureExtraCharges(Long[] structureExtraCharges) {
    this.structureExtraCharges = structureExtraCharges;
    validateStructureExtraCharges();
  }

  /**
   * Get the area limit array for structure extra charges. The array contains an
   * area limit per each structure charge, and each limit specifies the minimum
   * area for which the matching charge is applicable.
   *
   * @return the structureExtraChargeLimits
   */
  public Double[] getStructureExtraChargeLimits() {
    return structureExtraChargeLimits;
  }

  public void setStructureExtraChargeLimits(Double[] structureExtraChargeLimits) {
    this.structureExtraChargeLimits = structureExtraChargeLimits;
    validateStructureExtraCharges();
  }

  /**
   * @return the areaExtraCharges
   */
  public Long[] getAreaExtraCharges() {
    return areaExtraCharges;
  }

  /**
   * @param areaExtraCharges
   *          the areaExtraCharges to set
   */
  public void setAreaExtraCharges(Long[] areaExtraCharges) {
    this.areaExtraCharges = areaExtraCharges;
    validateAreaExtraCharges();
  }

  /**
   * @return the areaExtraChargeLimits
   */
  public Double[] getAreaExtraChargeLimits() {
    return areaExtraChargeLimits;
  }

  /**
   * @param areaExtraChargeLimits
   *          the areaExtraChargeLimits to set
   */
  public void setAreaExtraChargeLimits(Double[] areaExtraChargeLimits) {
    this.areaExtraChargeLimits = areaExtraChargeLimits;
    validateAreaExtraCharges();
  }

  public Integer getFixedLocationId() {
    return fixedLocationId;
  }

  public void setFixedLocationId(Integer fixedLocationId) {
    this.fixedLocationId = fixedLocationId;
  }

  private void validateAreaExtraCharges() {
    if (areaExtraCharges == null || areaExtraChargeLimits == null)
      return;
    if (areaExtraCharges.length != areaExtraChargeLimits.length) {
      throw new IllegalStateException("Area extra charge limits length mismatch");
    }
  }

  private void validateStructureExtraCharges() {
    if (structureExtraCharges == null || structureExtraChargeLimits == null)
      return;
    if (structureExtraCharges.length != structureExtraChargeLimits.length) {
      throw new IllegalStateException("Structure extra charge limits length mismatch");
    }
  }
}
