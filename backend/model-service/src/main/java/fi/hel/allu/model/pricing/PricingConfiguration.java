package fi.hel.allu.model.pricing;

public class PricingConfiguration {


  long baseCharge;
  int buildDiscountPercent;
  int durationDiscountPercent;
  int durationDiscountLimit;
  long[] structureExtraCharges;
  double[] structureExtraChargeLimits;
  long[] areaExtraCharges;
  double[] areaExtraChargeLimits;

  public PricingConfiguration(long baseCharge, int buildDiscountPercent, int durationDiscountPercent, int durationDiscountLimit,
      long[] structureExtraCharges, double[] structureExtraChargeLimits, long[] areaExtraCharges, double[] areaExtraChargeLimits) {
    super();
    this.baseCharge = baseCharge;
    this.buildDiscountPercent = buildDiscountPercent;
    this.durationDiscountPercent = durationDiscountPercent;
    this.durationDiscountLimit = durationDiscountLimit;
    this.structureExtraCharges = structureExtraCharges;
    this.structureExtraChargeLimits = structureExtraChargeLimits;
    this.areaExtraCharges = areaExtraCharges;
    this.areaExtraChargeLimits = areaExtraChargeLimits;
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
  public long[] getStructureExtraCharges() {
    return structureExtraCharges;
  }

  public void setStructureExtraCharges(long[] structureExtraCharges) {
    this.structureExtraCharges = structureExtraCharges;
  }

  /**
   * Get the area limit array for structure extra charges. The array contains an
   * area limit per each structure charge, and each limit specifies the minimum
   * area for which the matching charge is applicable.
   *
   * @return the structureExtraChargeLimits
   */
  public double[] getStructureExtraChargeLimits() {
    return structureExtraChargeLimits;
  }

  public void setStructureExtraChargeLimits(double[] structureExtraChargeLimits) {
    this.structureExtraChargeLimits = structureExtraChargeLimits;
  }

  /**
   * @return the areaExtraCharges
   */
  public long[] getAreaExtraCharges() {
    return areaExtraCharges;
  }

  /**
   * @param areaExtraCharges
   *          the areaExtraCharges to set
   */
  public void setAreaExtraCharges(long[] areaExtraCharges) {
    this.areaExtraCharges = areaExtraCharges;
  }

  /**
   * @return the areaExtraChargeLimits
   */
  public double[] getAreaExtraChargeLimits() {
    return areaExtraChargeLimits;
  }

  /**
   * @param areaExtraChargeLimits
   *          the areaExtraChargeLimits to set
   */
  public void setAreaExtraChargeLimits(double[] areaExtraChargeLimits) {
    this.areaExtraChargeLimits = areaExtraChargeLimits;
  }
}
