package fi.hel.allu.model.pricing;

public class InvoiceRowTag {
  private final String asString;

  private InvoiceRowTag(TagText txt) {
    this.asString = txt.toString();
  }

  private InvoiceRowTag(TagText txtStart, String ending) {
    this.asString = txtStart.toString() + "#" + ending;
  }

  public static InvoiceRowTag EventBaseFee() {
    return new InvoiceRowTag(TagText.EvtBf);
  }

  public static InvoiceRowTag EventDailyFee() {
    return new InvoiceRowTag(TagText.EvtDF);
  }

  public static InvoiceRowTag EventMultipleDayFee() {
    return new InvoiceRowTag(TagText.EvtMDF);
  }

  public static InvoiceRowTag EventLongEventDiscount() {
    return new InvoiceRowTag(TagText.EvtLED);
  }

  public static InvoiceRowTag EventBuildDayFee() {
    return new InvoiceRowTag(TagText.EvtBDF);
  }

  public static InvoiceRowTag EventHeavyStructures() {
    return new InvoiceRowTag(TagText.EvtHS);
  }

  public static InvoiceRowTag EventSalesActivity() {
    return new InvoiceRowTag(TagText.EvtSA);
  }

  public static InvoiceRowTag EventEcoCompass() {
    return new InvoiceRowTag(TagText.EvtEC);
  }

  public static InvoiceRowTag AreaRentalDailyFee(String areaId) {
    return new InvoiceRowTag(TagText.ARDF, areaId);
  }

  public static InvoiceRowTag AreaRentalHandlingFee() {
    return new InvoiceRowTag(TagText.ARHF);
  }

  public static InvoiceRowTag ExcavationAnnouncementDailyFee(String areaId) {
    return new InvoiceRowTag(TagText.EADF, areaId);
  }

  public static InvoiceRowTag ExcavationAnnonuncementHandlingFee() {
    return new InvoiceRowTag(TagText.EAHF);
  }

  public static InvoiceRowTag ShortTermRentalArt() {
    return new InvoiceRowTag(TagText.STRA);
  }

  public static InvoiceRowTag ShortTermRentalSmallArtAndCulture() {
    return new InvoiceRowTag(TagText.STRSAC);
  }

  public static InvoiceRowTag ShortTermRentalBenji() {
    return new InvoiceRowTag(TagText.STRBe);
  }

  public static InvoiceRowTag ShortTermRentalBridgeBanner() {
    return new InvoiceRowTag(TagText.STRBB);
  }

  public static InvoiceRowTag ShortTermRentalCircus() {
    return new InvoiceRowTag(TagText.STRCi);
  }

  public static InvoiceRowTag ShortTermRentalDogTrainingEvent() {
    return new InvoiceRowTag(TagText.STRDTE);
  }

  public static InvoiceRowTag ShortTermRentalDogTrainingField() {
    return new InvoiceRowTag(TagText.STRDTF);
  }

  public static InvoiceRowTag ShortTermRentalKeskuskatuSales() {
    return new InvoiceRowTag(TagText.STRKKS);
  }

  public static InvoiceRowTag ShortTermRentalKeskuskatuSalesLong() {
    return new InvoiceRowTag(TagText.STRKKSL);
  }

  public static InvoiceRowTag ShortTermRentalOther() {
    return new InvoiceRowTag(TagText.STROth);
  }

  public static InvoiceRowTag ShortTermRentalPromotionOrSales() {
    return new InvoiceRowTag(TagText.STRPOS);
  }

  public static InvoiceRowTag ShortTermRentalSeasonSale() {
    return new InvoiceRowTag(TagText.STRSS);
  }

  public static InvoiceRowTag ShortTermRentalSeasonSaleLong() {
    return new InvoiceRowTag(TagText.STRSSL);
  }

  public static InvoiceRowTag ShortTermRentalStorageArea() {
    return new InvoiceRowTag(TagText.STRSA);
  }

  public static InvoiceRowTag ShortTermRentalSummerTheater() {
    return new InvoiceRowTag(TagText.STRST);
  }

  public static InvoiceRowTag ShortTermRentalUrbanFarming() {
    return new InvoiceRowTag(TagText.STRUF);
  }

  @Override
  public String toString() {
    return asString;
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
    // Event, Heavy structures
    EvtHS,
    // Event, Sales activity
    EvtSA,
    // Event, Eco compass
    EvtEC,
    // Area rental, Daily fee
    ARDF,
    // Area rental, Handling fee
    ARHF,
    // Excavation announcement, Daily fee
    EADF,
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
    STRUF
  }

}
