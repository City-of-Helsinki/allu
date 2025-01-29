package fi.hel.allu.model.pricing;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.model.dao.TerminationDao;
import org.springframework.util.CollectionUtils;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.util.CalendarUtil;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.*;

public class ShortTermRentalPricing extends Pricing {
  private final Application application;
  private final List<Location> locations;
  private final PricingExplanator explanationService;
  private final PricingDao pricingDao;
  private final TerminationDao terminationDao;
  private final double applicationArea;
  private final boolean customerIsCompany;
  private final DecimalFormat decimalFormat;
  private final List<InvoicingPeriod> recurringPeriods;

  // Various price constants for short term rental
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
    static final String PROMOTION_OR_SALES_LARGE = "%s EUR/m²/kk + alv, yli 0,8 m seinästä";
    static final String STORAGE_AREA = "Varastoalue";
    static final String URBAN_FARMING = "Kaupunkiviljelypaikka yhdistyksille ja yhteisöille";
    static final String KESKUSKATU_SALES = "%s EUR/päivä/alkava 10 m² + alv";
    static final String SUMMER_THEATER = "%s EUR/toimintakuukausi";
    static final String DOG_TRAINING_FIELD_ORG = "Vuosivuokra yhdistyksille %s EUR/vuosi (2h/vk)";
    static final String DOG_TRAINING_FIELD_COM = "Vuosivuokra yrityksille %s EUR/vuosi (2h/vk)";
    static final String DOG_TRAINING_EVENT_ORG = "Koirankoulutustapahtuma, järjestäjänä yhdistys";
    static final String DOG_TRAINING_EVENT_COM = "Koirankoulutustapahtuma, järjestäjänä yritys";
    static final String SMALL_ART_AND_CULTURE = "Pienimuotoinen kaupallinen taide- ja kulttuuritoiminta";
    static final String SEASON_SALES = "%s EUR/päivä/alkava 10 m² + alv";
    static final String CIRCUS = "%s EUR/päivä + alv";
  }

  public ShortTermRentalPricing(Application application,
      PricingExplanator explanationService, PricingDao pricingDao, TerminationDao terminationDao,
      double applicationArea, boolean customerIsCompany, List<InvoicingPeriod> recurringPeriods, List<Location> locations) {
    super();
    this.application = application;
    this.applicationArea = applicationArea;
    this.customerIsCompany = customerIsCompany;
    this.explanationService = explanationService;
    this.pricingDao = pricingDao;
    this.terminationDao = terminationDao;
    this.recurringPeriods = recurringPeriods;
    this.decimalFormat = new DecimalFormat("#.00");
    this.locations = locations;
    final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator(',');
    decimalFormat.setDecimalFormatSymbols(symbols);
  }


  public void calculatePrice() {
    if (application.getKindsWithSpecifiers() == null) {
      return;
    }
    application.getKindsWithSpecifiers().keySet().forEach(kind -> calculatePrice(kind));
  }

  private void calculatePrice(ApplicationKind kind) {
    ShortTermRental str = (ShortTermRental) application.getExtension();
    boolean billableSalesArea = Optional.ofNullable(str).map(s -> s.getBillableSalesArea()).orElse(false);
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
      updatePricePerUnit(ChargeBasisTag.ShortTermRentalBenji(), ChronoUnit.DAYS,
          getPrice(PricingKey.BENJI_DAILY_PRICE, application.getStartTime()), InvoiceLines.BENJI);
      break;
    case BRIDGE_BANNER:
      // Non-commercial organizer: 150 EUR/week
      // Commercial organizer: 750 EUR/week
      updateBridgeBannerPrice();
      break;
    case CIRCUS: {
      final int price = getPrice(PricingKey.CIRCUS_DAILY_PRICE, application.getStartTime());
      updatePricePerUnit(ChargeBasisTag.ShortTermRentalCircus(), ChronoUnit.DAYS,
          price, priceText(price, InvoiceLines.CIRCUS));
      break;
    }
    case DOG_TRAINING_EVENT:
      // Associations: 50 EUR/event
      // Companies: 100 EUR/event
      if (customerIsCompany) {
        final int price = getPrice(PricingKey.DOG_TRAINING_EVENT_COMPANY_PRICE, application.getStartTime());
        setPriceInCents(price);
        addChargeBasisEntry(ChargeBasisTag.ShortTermRentalDogTrainingEvent(), ChargeBasisUnit.PIECE, 1,
            price, InvoiceLines.DOG_TRAINING_EVENT_COM, price, explanationService.getExplanation(application));
      } else {
        final int price = getPrice(PricingKey.DOG_TRAINING_EVENT_ASSOCIATION_PRICE, application.getStartTime());
        setPriceInCents(price);
        addChargeBasisEntry(ChargeBasisTag.ShortTermRentalDogTrainingEvent(), ChargeBasisUnit.PIECE, 1,
            price, InvoiceLines.DOG_TRAINING_EVENT_ORG, price, explanationService.getExplanation(application));
      }
      break;
    case DOG_TRAINING_FIELD:
      updateDogTrainingFieldPrice();
      break;
    case KESKUSKATU_SALES: {
      // 50 EUR/day/starting 10 sqm
      final int price = getPrice(PricingKey.KESKUSKATU_SALES_TEN_SQM_PRICE, application.getStartTime());
      updatePriceByTimeAndArea(price, ChronoUnit.DAYS, 10, false,
          priceText(price, InvoiceLines.KESKUSKATU_SALES), null, ChargeBasisTag.ShortTermRentalKeskuskatuSales(), null);
      break;
    }
    case OTHER:
      // Handler should set the price override
      break;
    case PROMOTION_OR_SALES:
      // at max 0.8 m from a wall: free of charge
      // over 0.8m from a wall: 2 EUR/sqm/kk
      if (billableSalesArea) {
        final int price = getPrice(PricingKey.PROMOTION_OR_SALES_MONTHLY, application.getStartTime());
        updatePriceByTimeAndArea(price, ChronoUnit.MONTHS, 1, false,
            priceText(price, InvoiceLines.PROMOTION_OR_SALES_LARGE), null,
            ChargeBasisTag.ShortTermRentalPromotionOrSales(), null);
      } else {
        // free of charge
        addChargeBasisEntry(ChargeBasisTag.ShortTermRentalPromotionOrSales(), ChargeBasisUnit.PIECE, 1, 0,
            InvoiceLines.PROMOTION_OR_SALES_SMALL, 0, explanationService.getExplanation(application));
        setPriceInCents(0);
      }
      break;
    case SEASON_SALE: {
      // 50 EUR/day/starting 10 sqm
      final int price = getPrice(PricingKey.SEASON_SALE_TEN_SQM_PRICE, application.getStartTime());
      updatePriceByTimeAndArea(price, ChronoUnit.DAYS, 10, false, priceText(price, InvoiceLines.SEASON_SALES),
          null, ChargeBasisTag.ShortTermRentalSeasonSale(), null);
      break;
    }
    case STORAGE_AREA:
      // 0.50 EUR/sqm/month
      updatePriceByTimeAndArea(getPrice(PricingKey.STORAGE_AREA_MONTHLY_PRICE, application.getStartTime()), ChronoUnit.MONTHS, 1, false,
          InvoiceLines.STORAGE_AREA, null, ChargeBasisTag.ShortTermRentalStorageArea(), null);
      break;
    case SUMMER_THEATER: {
      // 120 EUR/month
      final int price = getPrice(PricingKey.SUMMER_THEATER_YEARLY_PRICE, application.getStartTime());
      updatePricePerUnit(ChargeBasisTag.ShortTermRentalSummerTheater(), ChronoUnit.MONTHS,
          price, priceText(price, InvoiceLines.SUMMER_THEATER));
      break;
    }
    case URBAN_FARMING:
      updateUrbanFarmingPrice();
      break;
    case SUMMER_TERRACE:
    case WINTER_TERRACE:
      if (billableSalesArea) {
        updateTerracePrice();
      }
      break;
    case PARKLET:
      updateTerracePrice();
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

    final int urbanFarmingTermPrice = getPrice(PricingKey.URBAN_FARMING_TERM_PRICE, application.getStartTime());
    double billableArea = getBillableArea();
    int netPrice = urbanFarmingTermPrice * (int) billableArea * numTerms;

    addChargeBasisEntry(ChargeBasisTag.ShortTermRentalUrbanFarming(), ChargeBasisUnit.SQUARE_METER, billableArea,
        urbanFarmingTermPrice * numTerms,
        InvoiceLines.URBAN_FARMING, netPrice, explanationService.getExplanation(application));
    setPriceInCents(netPrice);
  }


  private double getBillableArea() {
    return applicationArea == 0.0 ? 0.0 : Math.ceil(applicationArea);
  }

  private int getPrice(PricingKey key, ZonedDateTime startTime) {
    return pricingDao.findValue(ApplicationType.SHORT_TERM_RENTAL, key, startTime);
  }

  private String priceText(int priceInCents, String text) {
    final double euroPrice = priceInCents / 100.0;
    String priceText = decimalFormat.format(euroPrice);
    if (priceText.endsWith(",00")) {
      priceText = priceText.substring(0, priceText.length() - 3);
    }
    return String.format(text, priceText);
  }

  private void updateTerracePrice() {
    String paymentClass = getPaymentClass(application);
    // Price per square meter per month
    int unitPrice = pricingDao.findValue(ApplicationType.SHORT_TERM_RENTAL,
        PricingKey.forTerraceKind(application.getKind()), paymentClass, application.getStartTime());
    getTerracePrices(paymentClass, unitPrice).forEach(p -> addTerracePeriodPrice(p));
  }

  private void addTerracePeriodPrice(TerracePrice price) {
    addChargeBasisEntry(price.getTag(), ChargeBasisUnit.MONTH, price.getNumberOfBillableMonths(),
        price.getMonthlyPrice(),
        ApplicationKind.PARKLET.equals(application.getKind()) ? price.getParkletInvoiceLineText() : price.getInvoiceLineText(),
        price.getNetPrice(),
        explanationService.getExplanationWithCustomPeriod(application, price.getPricePeriod()),
        null, price.getInvoicingPeriodId(), null);
    setPriceInCents(getPriceInCents() + price.getNetPrice());
  }

  private Stream<TerracePrice> getTerracePrices(String paymentClass, int unitPrice) {
    if (CollectionUtils.isEmpty(recurringPeriods)) {
      return Stream.of(new TerracePrice(unitPrice, (int)getBillableArea(), application.getStartTime(), getActualEndTime(application), application));
    } else {
      return recurringPeriods.stream()
          .map(p -> new TerracePrice(unitPrice, (int)getBillableArea(), p.getStartTime(), p.getEndTime(), application, p.getId()));
    }
  }

  private String getPaymentClass(Application application) {
    return application.getLocations().get(0).getEffectivePaymentTariff();
  }

  private void updateBridgeBannerPrice() {
    int centsPerUnit = isCommercial() ? getPrice(PricingKey.BRIDGE_BANNER_WEEKLY_PRICE_COMMERCIAL, application.getStartTime()) :
      getPrice(PricingKey.BRIDGE_BANNER_WEEKLY_PRICE_NONCOMMERCIAL, application.getStartTime());
    String invoiceLine = isCommercial() ? InvoiceLines.BANDEROL_COMMERCIAL : InvoiceLines.BANDEROL_NONCOMMERCIAL;
    pricePerFixedLocation(centsPerUnit, ChronoUnit.WEEKS, invoiceLine, ChargeBasisTag::ShortTermRentalBridgeBanner);
  }

  private void updateDogTrainingFieldPrice() {
    int centsPerUnit = customerIsCompany ? getPrice(PricingKey.DOG_TRAINING_FIELD_YEARLY_COMPANY, application.getStartTime())
        : getPrice(PricingKey.DOG_TRAINING_FIELD_YEARLY_ASSOCIATION, application.getStartTime());
    String invoiceLine = customerIsCompany ? priceText(centsPerUnit, InvoiceLines.DOG_TRAINING_FIELD_COM) :
      priceText(centsPerUnit, InvoiceLines.DOG_TRAINING_FIELD_ORG);
    pricePerFixedLocation(centsPerUnit, ChronoUnit.YEARS, invoiceLine, ChargeBasisTag::ShortTermRentalDogTrainingField);
  }

  private void pricePerFixedLocation(int centsPerUnit, ChronoUnit unit, String invoiceLine,
      Function<Integer, ChargeBasisTag> fixedLocationToTag) {
    int units = (int) CalendarUtil.startingUnitsBetween(application.getStartTime(), application.getEndTime(), unit);
    int priceInCents = centsPerUnit * units;
    Location location = locations.get(0);
    // Charge basis entry for each selected fixed location
    location.getFixedLocationIds().forEach(fixedLocationId -> {
      addChargeBasisEntry(fixedLocationToTag.apply(fixedLocationId), toChargeBasisUnit(unit), units, centsPerUnit, invoiceLine,
          priceInCents, explanationService.getExplanation(location, fixedLocationId));
      setPriceInCents(getPriceInCents() + priceInCents);
    });

  }

  private ZonedDateTime getActualEndTime(Application application) {
    TerminationInfo termination = terminationDao.getTerminationInfo(application.getId());
    if (termination != null && termination.getTerminationDecisionTime() != null &&
        isTerminationWithinApplicationStartAndEndTimes(termination, application)) {
      return termination.getExpirationTime();
    }
    else {
      return application.getEndTime();
    }
  }

  private boolean isTerminationWithinApplicationStartAndEndTimes(TerminationInfo termination, Application application) {
    return (termination.getExpirationTime().isEqual(application.getStartTime()) ||
            termination.getExpirationTime().isAfter(application.getStartTime())) &&
            termination.getExpirationTime().isBefore(application.getEndTime());
  }

}
