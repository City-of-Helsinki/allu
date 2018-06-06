package fi.hel.allu.model.pricing;

import fi.hel.allu.common.util.CalendarUtil;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ShortTermRental;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class ShortTermRentalPricing extends Pricing {
  private final Application application;
  private final PricingExplanator explanationService;
  private final double applicationArea;
  private final boolean customerIsCompany;

  // Various price constants for short term rental
  private static final int BENJI_DAILY_PRICE = 32000; // 320 EUR/day
  private static final int BRIDGE_BANNER_WEEKLY_PRICE_COMMERCIAL = 75000; // 750
                                                                          // EUR/week
  private static final int BRIDGE_BANNER_WEEKLY_PRICE_NONCOMMERCIAL = 15000; // 150
                                                                             // EUR/week
  private static final int CIRCUS_DAILY_PRICE = 20000; // 200 EUR/day
  private static final int DOG_TRAINING_EVENT_ASSOCIATION_PRICE = 5000; // 50
                                                                        // EUR
  private static final int DOG_TRAINING_EVENT_COMPANY_PRICE = 10000; // 100 EUR
  private static final int DOG_TRAINING_FIELD_YEARLY_COMPANY = 30000; // 300 EUR/year
  private static final int DOG_TRAINING_FIELD_YEARLY_ASSOCIATION = 10000; // 100
                                                                          // EUR/year
  private static final int KESKUSKATU_SALES_TEN_SQM_PRICE = 5000; // 50
                                                                  // EUR/10sqm/day
  private static final int PROMOTION_OR_SALES_MONTHLY = 200; // 2 EUR/sqm/kk
  private static final int SEASON_SALE_TEN_SQM_PRICE = 5000; // 50 EUR/10sqm/day
  private static final int STORAGE_AREA_MONTHLY_PRICE = 50;  // 0.50 EUR/sqm/month
  private static final int SUMMER_THEATER_YEARLY_PRICE = 12000; // 120 EUR/year
  private static final int URBAN_FARMING_TERM_PRICE = 200; // 2.00 EUR/sqm/term
  private static final int LONG_TERM_DISCOUNT_LIMIT = 14; // how many days
                                                          // before discount?

  /*
   * Invoice line string constants
   */
  static class InvoiceLines {
    static final String ART = "Taideteos";
    static final String BANDEROL_NONCOMMERCIAL = "Banderollit silloissa, ei-kaupallinen";
    static final String BANDEROL_COMMERCIAL = "Banderollit silloissa, kaupallinen";
    static final String BENJI = "Benji-hyppylaite";
    static final String OTHER_SHORT_TERM_RENTAL = "Muu lyhytaikainen maanvuokraus";
    static final String PROMOTION_OR_SALES_SMALL = "Korkeintaan 0,8 m seinästä";
    static final String PROMOTION_OR_SALES_LARGE = "2€/m2/kk + alv, yli 0,8 m seinästä";
    static final String STORAGE_AREA = "Varastoalue";
    static final String URBAN_FARMING = "Kaupunkiviljelypaikka yhdistyksille ja yhteisöille";
    static final String KESKUSKATU_SALES = "50 €/päivä/alkava 10 m² + alv";
    static final String SUMMER_THEATER = "120 €/toimintakuukausi";
    static final String DOG_TRAINING_FIELD_ORG = "Vuosivuokra yhdistyksille 100 €/vuosi (2h/vk)";
    static final String DOG_TRAINING_FIELD_COM = "Vuosivuokra yrityksille 300 €/vuosi (2h/vk)";
    static final String DOG_TRAINING_EVENT_ORG = "Koirankoulutustapahtuma, järjestäjänä yhdistys";
    static final String DOG_TRAINING_EVENT_COM = "Koirankoulutustapahtuma, järjestäjänä yritys";
    static final String SMALL_ART_AND_CULTURE = "Pienimuotoinen kaupallinen taide- ja kulttuuritoiminta";
    static final String SEASON_SALES = "50 €/päivä/alkava 10 m² + alv";
    static final String CIRCUS = "200 €/päivä + alv";
  }

  public ShortTermRentalPricing(Application application, PricingExplanator explanationService, double applicationArea, boolean customerIsCompany) {
    super();
    this.application = application;
    this.applicationArea = applicationArea;
    this.customerIsCompany = customerIsCompany;
    this.explanationService = explanationService;
  }


  public void calculatePrice() {
    if (application.getKindsWithSpecifiers() == null) {
      return;
    }
    application.getKindsWithSpecifiers().keySet().forEach(kind -> calculatePrice(kind));
  }

  private void calculatePrice(ApplicationKind kind) {
    switch (kind) {
    case ART:
      // Free event
      setPriceInCents(0);
      addChargeBasisEntry(ChargeBasisTag.ShortTermRentalArt(), ChargeBasisUnit.PIECE, 1.0, 0,
          InvoiceLines.ART, 0, explanationService.getExplanation(application));
      break;
    case SMALL_ART_AND_CULTURE:
      // Free event
      setPriceInCents(0);
      addChargeBasisEntry(ChargeBasisTag.ShortTermRentalSmallArtAndCulture(), ChargeBasisUnit.PIECE, 1.0, 0,
          InvoiceLines.SMALL_ART_AND_CULTURE, 0, explanationService.getExplanation(application));
      break;
    case BENJI:
      // 320 EUR/day
        updatePricePerUnit(ChargeBasisTag.ShortTermRentalBenji(), ChronoUnit.DAYS, BENJI_DAILY_PRICE,
            InvoiceLines.BENJI);
      break;
    case BRIDGE_BANNER:
      // Non-commercial organizer: 150 EUR/week
      // Commercial organizer: 750 EUR/week
      if (isCommercial()) {
          updatePricePerUnit(ChargeBasisTag.ShortTermRentalBridgeBanner(), ChronoUnit.WEEKS,
              BRIDGE_BANNER_WEEKLY_PRICE_COMMERCIAL, InvoiceLines.BANDEROL_COMMERCIAL);
      } else {
          updatePricePerUnit(ChargeBasisTag.ShortTermRentalBridgeBanner(), ChronoUnit.WEEKS,
              BRIDGE_BANNER_WEEKLY_PRICE_NONCOMMERCIAL,
            InvoiceLines.BANDEROL_NONCOMMERCIAL);
      }
      break;
    case CIRCUS:
        updatePricePerUnit(ChargeBasisTag.ShortTermRentalCircus(), ChronoUnit.DAYS, CIRCUS_DAILY_PRICE,
            InvoiceLines.CIRCUS);
      break;
    case DOG_TRAINING_EVENT:
      // Associations: 50 EUR/event
      // Companies: 100 EUR/event
      if (customerIsCompany) {
        setPriceInCents(DOG_TRAINING_EVENT_COMPANY_PRICE);
          addChargeBasisEntry(ChargeBasisTag.ShortTermRentalDogTrainingEvent(), ChargeBasisUnit.PIECE, 1,
              DOG_TRAINING_EVENT_COMPANY_PRICE, InvoiceLines.DOG_TRAINING_EVENT_COM,
            DOG_TRAINING_EVENT_COMPANY_PRICE, explanationService.getExplanation(application));
      } else {
        setPriceInCents(DOG_TRAINING_EVENT_ASSOCIATION_PRICE);
          addChargeBasisEntry(ChargeBasisTag.ShortTermRentalDogTrainingEvent(), ChargeBasisUnit.PIECE, 1,
              DOG_TRAINING_EVENT_ASSOCIATION_PRICE, InvoiceLines.DOG_TRAINING_EVENT_ORG,
            DOG_TRAINING_EVENT_ASSOCIATION_PRICE, explanationService.getExplanation(application));
      }
      break;
    case DOG_TRAINING_FIELD:
      // Associations: 100 EUR/year
      // Companies: 300 EUR/year
      if (customerIsCompany) {
          updatePricePerUnit(ChargeBasisTag.ShortTermRentalDogTrainingField(), ChronoUnit.YEARS,
              DOG_TRAINING_FIELD_YEARLY_COMPANY, InvoiceLines.DOG_TRAINING_FIELD_COM);
      } else {
          updatePricePerUnit(ChargeBasisTag.ShortTermRentalDogTrainingField(), ChronoUnit.YEARS,
              DOG_TRAINING_FIELD_YEARLY_ASSOCIATION,
            InvoiceLines.DOG_TRAINING_FIELD_ORG);
      }
      break;
    case KESKUSKATU_SALES:
      // 50 EUR/day/starting 10 sqm
      updatePriceByTimeAndArea(KESKUSKATU_SALES_TEN_SQM_PRICE, ChronoUnit.DAYS, 10, false,
            InvoiceLines.KESKUSKATU_SALES, null, ChargeBasisTag.ShortTermRentalKeskuskatuSales(), null);
      break;
    case OTHER:
      // Handler should set the price override
      break;
    case PROMOTION_OR_SALES:
      // at max 0.8 m from a wall: free of charge
      // over 0.8m from a wall: 2 EUR/sqm/kk
      ShortTermRental str = (ShortTermRental) application.getExtension();
      if (str != null && Optional.ofNullable(str.getBillableSalesArea()).orElse(false) == true) {
        updatePriceByTimeAndArea(PROMOTION_OR_SALES_MONTHLY, ChronoUnit.MONTHS, 1, false,
              InvoiceLines.PROMOTION_OR_SALES_LARGE, null,
              ChargeBasisTag.ShortTermRentalPromotionOrSales(), null);
      } else {
        // free of charge
          addChargeBasisEntry(ChargeBasisTag.ShortTermRentalPromotionOrSales(), ChargeBasisUnit.PIECE, 1, 0,
              InvoiceLines.PROMOTION_OR_SALES_SMALL, 0, explanationService.getExplanation(application));
        setPriceInCents(0);
      }
      break;
    case SEASON_SALE:
      // 50 EUR/day/starting 10 sqm
      updatePriceByTimeAndArea(SEASON_SALE_TEN_SQM_PRICE, ChronoUnit.DAYS, 10, false, InvoiceLines.SEASON_SALES,
            null, ChargeBasisTag.ShortTermRentalSeasonSale(), null);
      break;
    case STORAGE_AREA:
      // 0.50 EUR/sqm/month
        updatePriceByTimeAndArea(STORAGE_AREA_MONTHLY_PRICE, ChronoUnit.MONTHS, 1, false, InvoiceLines.STORAGE_AREA,
            null,
            ChargeBasisTag.ShortTermRentalStorageArea(), null);
      break;
    case SUMMER_THEATER:
      // 120 EUR/month
        updatePricePerUnit(ChargeBasisTag.ShortTermRentalSummerTheater(), ChronoUnit.MONTHS, SUMMER_THEATER_YEARLY_PRICE,
            InvoiceLines.SUMMER_THEATER);
      break;
    case URBAN_FARMING:
      updateUrbanFarmingPrice();
      break;
    default:
      break;
    }
  }

  /*
   * Calculate application's price using the application period. Price is
   * calculated as [centsPerUnit] * [starting units] and stored into
   * application.
   */
  private void updatePricePerUnit(ChargeBasisTag chargeBasisTag, ChronoUnit chronoUnit, int centsPerUnit,
      String chargeBasisText) {
    final int units = (int) CalendarUtil.startingUnitsBetween(application.getStartTime(), application.getEndTime(),
        chronoUnit);
    int priceInCents = centsPerUnit * units;
    addChargeBasisEntry(chargeBasisTag, toChargeBasisUnit(chronoUnit), units, centsPerUnit, chargeBasisText,
        priceInCents, explanationService.getExplanation(application));
    setPriceInCents(priceInCents);
  }

  private boolean isCommercial() {
    final ShortTermRental str = (ShortTermRental) application.getExtension();
    if (str != null && str.getCommercial() != null) {
      return str.getCommercial();
    }
    // if commercial-flag is not available, assume false
    return false;
  }

  /**
   * Update price based on application time and area
   *
   * @param priceInCents unit price in cents
   * @param pricePeriod billing time unit
   * @param priceArea billing area unit in square meters
   * @param longTermDiscount should long time discount be applied?
   * @param chargeBasisText explanation text for full-price charge basis entries
   * @param chargeBasisTextLongTerm explanation text for discounted charge basis
   *          entries
   * @param chargeBasisTag charge basis tag for full-price charge basis entries
   * @param chargeBasisTagLongTerm charge basis tag for discounted charge basis
   *          entries
   */
  private void updatePriceByTimeAndArea(int priceInCents,
      ChronoUnit pricePeriod, int priceArea, boolean longTermDiscount,
      String chargeBasisText, String chargeBasisTextLongTerm, ChargeBasisTag chargeBasisTag,
      ChargeBasisTag chargeBasisTagLongTerm) {
    final int numTimeUnits = (int) CalendarUtil.startingUnitsBetween(application.getStartTime(),
        application.getEndTime(), pricePeriod);

    final int numAreaUnits = (int) Math.ceil(applicationArea / priceArea);
    // How many time units are charged full price?
    int fullPriceUnits = longTermDiscount ? Math.min(numTimeUnits, LONG_TERM_DISCOUNT_LIMIT) : numTimeUnits;
    long price = numAreaUnits * fullPriceUnits * priceInCents;
    addChargeBasisEntry(chargeBasisTag, toChargeBasisUnit(pricePeriod), fullPriceUnits, numAreaUnits * priceInCents,
        chargeBasisText, (int) price,  explanationService.getExplanation(application));

    if (longTermDiscount == true && numTimeUnits > LONG_TERM_DISCOUNT_LIMIT) {
      // 50% discount for extra days
      final int numDiscountUnits = numTimeUnits - LONG_TERM_DISCOUNT_LIMIT;
      long discountPrice = numAreaUnits * numDiscountUnits * priceInCents / 2;
      addChargeBasisEntry(chargeBasisTagLongTerm, toChargeBasisUnit(pricePeriod), numDiscountUnits,
          numAreaUnits * priceInCents / 2,
          chargeBasisTextLongTerm, (int) discountPrice, explanationService.getExplanation(application));
      price += discountPrice;
    }
    setPriceInCents((int) price);
  }

  private void updateUrbanFarmingPrice() {
    // 2 EUR/sqm/term
    int numTerms = 1;
    if (application.getEndTime() != null && application.getStartTime() != null) {
      numTerms = application.getEndTime().getYear() - application.getStartTime().getYear() + 1;
    }

    double billableArea = applicationArea == 0.0 ? 0.0 : Math.ceil(applicationArea);
    int netPrice = URBAN_FARMING_TERM_PRICE * (int) billableArea * numTerms;

    addChargeBasisEntry(ChargeBasisTag.ShortTermRentalUrbanFarming(), ChargeBasisUnit.SQUARE_METER, billableArea,
        URBAN_FARMING_TERM_PRICE * numTerms,
        InvoiceLines.URBAN_FARMING, netPrice, explanationService.getExplanation(application));
    setPriceInCents(netPrice);
  }

}
