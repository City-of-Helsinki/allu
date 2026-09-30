package fi.hel.allu.model.pricing;

import fi.hel.allu.model.domain.Location;

public class ChargeBasisTag {
  private final String asString;
  private boolean referrable = true;

  private ChargeBasisTag(TagText txt) {
    this.asString = txt.toString();
  }

  private ChargeBasisTag(TagText txt, boolean referrable) {
    this.asString = txt.toString();
    this.referrable = referrable;
  }


  private ChargeBasisTag(TagText txtStart, String ending) {
    this.asString = txtStart.toString() + "#" + ending;
  }

  private ChargeBasisTag(TagText txtStart, String ending, boolean referrable) {
    this.asString = txtStart.toString() + "#" + ending;
    this.referrable = referrable;
  }

  public static ChargeBasisTag EventBaseFee() {
    return new ChargeBasisTag(TagText.EvtBf);
  }

  public static ChargeBasisTag EventDailyFee() {
    return new ChargeBasisTag(TagText.EvtDF);
  }

  public static ChargeBasisTag EventMultipleDayFee(Integer locationId) {
    if (locationId != null) {
      return new ChargeBasisTag(TagText.EvtMDF, locationId.toString());
    }
    return new ChargeBasisTag(TagText.EvtMDF);
  }

  public static ChargeBasisTag EventLongEventDiscount(Integer locationId) {
    if (locationId != null) {
      return new ChargeBasisTag(TagText.EvtLED, locationId.toString());
    }
    return new ChargeBasisTag(TagText.EvtLED);
  }

  public static ChargeBasisTag EventBuildDayFee(Integer locationId) {
    if (locationId != null) {
      return new ChargeBasisTag(TagText.EvtBDF, locationId.toString());
    }
    return new ChargeBasisTag(TagText.EvtBDF);
  }

  public static ChargeBasisTag EventFreeEventDiscount() {
    return new ChargeBasisTag(TagText.EvtFED);
  }

  public static ChargeBasisTag EventEcoCompass() {
    return new ChargeBasisTag(TagText.EvtEC);
  }

  public static ChargeBasisTag AreaRentalDailyFee(String areaId) {
    return new ChargeBasisTag(TagText.ARDF, areaId);
  }

  public static ChargeBasisTag AreaRentalDailyFee(String areaId, String periodId) {
    return new ChargeBasisTag(TagText.ARDF, areaId + "#" + periodId);
  }

  public static ChargeBasisTag AreaRentalUnderpass(String areaId) {
    return new ChargeBasisTag(TagText.ARUP, areaId, false);
  }

  public static ChargeBasisTag AreaRentalUnderpass(String areaId, String periodId) {
    return new ChargeBasisTag(TagText.ARUP, areaId + "#" + periodId, false);
  }

  public static ChargeBasisTag AreaRentalHandlingFee() {
    return new ChargeBasisTag(TagText.ARHF);
  }

  public static ChargeBasisTag ExcavationAnnouncementDailyFee(String areaId) {
    return new ChargeBasisTag(TagText.EADF, areaId);
  }

  public static ChargeBasisTag ExcavationAnnouncementDailyFeeAdd(String areaId) {
    return new ChargeBasisTag(TagText.EADFADD, areaId);
  }


  public static ChargeBasisTag ExcavationAnnonuncementHandlingFee() {
    return new ChargeBasisTag(TagText.EAHF);
  }

  public static ChargeBasisTag ShortTermRentalArt() {
    return new ChargeBasisTag(TagText.STRA);
  }

  public static ChargeBasisTag ShortTermRentalSmallArtAndCulture() {
    return new ChargeBasisTag(TagText.STRSAC);
  }

  public static ChargeBasisTag ShortTermRentalBenji() {
    return new ChargeBasisTag(TagText.STRBe);
  }

  public static ChargeBasisTag ShortTermRentalBridgeBanner(Integer fixedLocationId) {
    return new ChargeBasisTag(TagText.STRBB, fixedLocationId.toString());
  }

  public static ChargeBasisTag ShortTermRentalCircus() {
    return new ChargeBasisTag(TagText.STRCi);
  }

  public static ChargeBasisTag ShortTermRentalDogTrainingEvent() {
    return new ChargeBasisTag(TagText.STRDTE);
  }

  public static ChargeBasisTag ShortTermRentalDogTrainingField(Integer fixedLocationId) {
    return new ChargeBasisTag(TagText.STRDTF, fixedLocationId.toString());
  }

  public static ChargeBasisTag ShortTermRentalKeskuskatuSales() {
    return new ChargeBasisTag(TagText.STRKKS);
  }

  public static ChargeBasisTag ShortTermRentalKeskuskatuSalesLong() {
    return new ChargeBasisTag(TagText.STRKKSL);
  }

  public static ChargeBasisTag ShortTermRentalOther() {
    return new ChargeBasisTag(TagText.STROth);
  }

  public static ChargeBasisTag ShortTermRentalMobileSales() {
    return new ChargeBasisTag(TagText.STRMS);
  }

  public static ChargeBasisTag ShortTermRentalPromotionOrSales() {
    return new ChargeBasisTag(TagText.STRPOS);
  }

  public static ChargeBasisTag ShortTermRentalSeasonSale() {
    return new ChargeBasisTag(TagText.STRSS);
  }

  public static ChargeBasisTag ShortTermRentalSeasonSaleLong() {
    return new ChargeBasisTag(TagText.STRSSL);
  }

  public static ChargeBasisTag ShortTermRentalStorageArea() {
    return new ChargeBasisTag(TagText.STRSA);
  }

  public static ChargeBasisTag ShortTermRentalSummerTheater() {
    return new ChargeBasisTag(TagText.STRST);
  }

  public static ChargeBasisTag ShortTermRentalUrbanFarming() {
    return new ChargeBasisTag(TagText.STRUF);
  }

  public static ChargeBasisTag AreaUsageTag() {
    return new ChargeBasisTag(TagText.ArUs);
  }

  public static ChargeBasisTag SummerTerrace(Integer periodId) {
    return periodId != null ? new ChargeBasisTag(TagText.STST, periodId.toString()) : new ChargeBasisTag(TagText.STST);
  }

  public static ChargeBasisTag WinterTerrace(Integer periodId) {
    return periodId != null ? new ChargeBasisTag(TagText.STWT, periodId.toString()) : new ChargeBasisTag(TagText.STWT);
  }

  public static ChargeBasisTag Parklet(Integer periodId) {
    return periodId != null ? new ChargeBasisTag(TagText.STPa, periodId.toString()) : new ChargeBasisTag(TagText.STPa);
  }

  public static ChargeBasisTag EcoCompassTag() {
    return new ChargeBasisTag(TagText.ECO, false);
  }

  @Override
  public String toString() {
    return asString;
  }

  public boolean isReferrable() {
    return referrable;
  }


/* Enumerations for different tag texts: */
private enum TagText {
    // Event, Base fee
    EvtBf,
    // Event, Daily fee
    EvtDF,
    // Event, Multiple day fee
    EvtMDF,
    // Event, Long event discount
    EvtLED,
    // Event, Buid day fee
    EvtBDF,
    // Event, Free event discount
    EvtFED,
    // Event, Eco compass
    EvtEC,
    // Area rental, Daily fee
    ARDF,
    // Area rental, Handling fee
    ARHF,
    // Area rental, underpass discount
    ARUP,
    // Excavation announcement, Daily fee
    EADF,
    // Excavation announcement, Daily fee for additional period
    EADFADD,
    // Excavation announcement, Handling fee
    EAHF,
    // Short term rental, Art
    STRA,
    // Short term rental, Small art and culture
    STRSAC,
    // Short term rental, Benji
    STRBe,
    // Short term rental, Bridge or banner
    STRBB,
    // Short term rental, Circus
    STRCi,
    // Short term rental, Dog training event
    STRDTE,
    // Short term rental, Dog training field
    STRDTF,
    // Short term rental, Keskuskatu sales
    STRKKS,
    // Short term rental, Keskuskatu sales - long
    STRKKSL,
    // Short term rental, Other
    STROth,
    // Short term rental, Promotion or sales
    STRPOS,
    // Short term rental, Season sale
    STRSS,
    // Short term rental, Season sale - long
    STRSSL,
    // Short term rental, Storage area
    STRSA,
    // Short term rental, Summer theater
    STRST,
    // Short term rental, Urban farming
    STRUF,
    // Short term rental, Summer terrace
    STST,
    // Short term rental, Winter terrace
    STWT,
    // Short term rental, Parklet
    STRMS,
    // Short term rental, Mobile sales
    STPa,
    // Area Usage
    ArUs,
    // Eco compass
    ECO
  }
}
