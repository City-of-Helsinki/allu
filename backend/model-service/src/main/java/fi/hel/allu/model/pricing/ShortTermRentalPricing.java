package fi.hel.allu.model.pricing;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ShortTermRental;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class ShortTermRentalPricing extends Pricing {
  private int priceInCents;

  private final Application application;
  private final double applicationArea;
  private final boolean applicantIsCompany;

  // Various price constants for short term rental
  private static final int BRIDGE_BANNER_WEEKLY_PRICE_COMMERCIAL = 75000; // 750
                                                                          // EUR/week
  private static final int BRIDGE_BANNER_WEEKLY_PRICE_NONCOMMERCIAL = 15000; // 150
                                                                             // EUR/week
  private static final int CIRCUS_DAILY_PRICE = 20000; // 200 EUR/day
  private static final int DOG_TRAINING_EVENT_ASSOCIATION_PRICE = 5000; // 50
                                                                        // EUR
  private static final int DOG_TRAINING_EVENT_COMPANY_PRICE = 10000; // 100 EUR
  private static final int DOG_TRAINING_FIELD_YEARLY_COMPANY = 20000; // 200
                                                                      // EUR/year
  private static final int DOG_TRAINING_FIELD_YEARLY_ASSOCIATION = 10000; // 100
                                                                          // EUR/year
  private static final int KESKUSKATU_SALES_TEN_SQM_PRICE = 5000; // 50
                                                                  // EUR/10sqm/day
  private static final int PROMOTION_OR_SALES_LARGE_YEARLY = 15000; // 150
                                                                    // EUR/year
  private static final int SEASON_SALE_TEN_SQM_PRICE = 5000; // 50 EUR/10sqm/day
  private static final int SUMMER_THEATER_YEARLY_PRICE = 12000; // 120 EUR/year
  private static final int URBAN_FARMING_TERM_PRICE = 200; // 2.00 EUR/sqm/term
  private static final int LONG_TERM_DISCOUNT_LIMIT = 14; // how many days
                                                          // before discount?

  public ShortTermRentalPricing(Application application, double applicationArea, boolean applicantIsCompany) {
    super();
    this.application = application;
    this.applicationArea = applicationArea;
    this.applicantIsCompany = applicantIsCompany;
    this.priceInCents = 0;
  }

  public int getPriceInCents() {
    return priceInCents;
  }

  private void setPriceInCents(int priceInCents) {
    this.priceInCents = priceInCents;
  }

  public void calculatePrice() {
    switch (application.getKind()) {
    case ART:
      // Free event
      setPriceInCents(0);
      break;
    case SMALL_ART_AND_CULTURE:
      // Free event
      setPriceInCents(0);
      break;
    case BENJI:
      // Price not defined
      setPriceInCents(0);
      break;
    case BRIDGE_BANNER:
      // Non-commercial organizer: 150 EUR/week
      // Commercial organizer: 750 EUR/week
      if (isCommercial()) {
        updatePricePerUnit(ChronoUnit.WEEKS, BRIDGE_BANNER_WEEKLY_PRICE_COMMERCIAL);
      } else {
        updatePricePerUnit(ChronoUnit.WEEKS, BRIDGE_BANNER_WEEKLY_PRICE_NONCOMMERCIAL);
      }
      break;
    case CIRCUS:
      updatePricePerUnit(ChronoUnit.DAYS, CIRCUS_DAILY_PRICE);
      break;
    case DOG_TRAINING_EVENT:
      // Associations: 50 EUR/event
      // Companies: 100 EUR/event
      if (applicantIsCompany) {
        setPriceInCents(DOG_TRAINING_EVENT_COMPANY_PRICE);
      } else {
        setPriceInCents(DOG_TRAINING_EVENT_ASSOCIATION_PRICE);
      }
      break;
    case DOG_TRAINING_FIELD:
      // Associations: 100 EUR/year
      // Companies: 200 EUR/year
      if (applicantIsCompany) {
        updatePricePerUnit(ChronoUnit.YEARS, DOG_TRAINING_FIELD_YEARLY_COMPANY);
      } else {
        updatePricePerUnit(ChronoUnit.YEARS, DOG_TRAINING_FIELD_YEARLY_ASSOCIATION);
      }
      break;
    case KESKUSKATU_SALES:
      // 1..14 days: 50 EUR/day/starting 10 sqm
      // 50% discount from 15. day onwards
      updatePriceByTimeAndArea(KESKUSKATU_SALES_TEN_SQM_PRICE, ChronoUnit.DAYS, 10, true);
      break;
    case OTHER_SHORT_TERM_RENTAL:
      // Unknown price
      setPriceInCents(0);
      break;
    case PROMOTION_OR_SALES:
      // 0.8 x 3.0 sqm: free of charge
      // bigger: 150 EUR/year
      ShortTermRental str = (ShortTermRental) application.getExtension();
      if (str != null && str.getLargeSalesArea() == true) {
        updatePricePerUnit(ChronoUnit.YEARS, PROMOTION_OR_SALES_LARGE_YEARLY);
      } else {
        // free of charge
        setPriceInCents(0);
      }
      break;
    case SEASON_SALE:
      // 1..14 days: 50 EUR/day/starting 10 sqm
      // 50% discount from 15. day onwards
      updatePriceByTimeAndArea(SEASON_SALE_TEN_SQM_PRICE, ChronoUnit.DAYS, 10, true);
      break;
    case STORAGE_AREA:
      // Unknown price
      setPriceInCents(0);
      break;
    case SUMMER_THEATER:
      // 120 EUR/month
      updatePricePerUnit(ChronoUnit.MONTHS, SUMMER_THEATER_YEARLY_PRICE);
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
  private void updatePricePerUnit(ChronoUnit chronoUnit, int centsPerUnit) {
    final int units = amountOfStartingUnits(application.getStartTime(), application.getEndTime(), chronoUnit);
    setPriceInCents(centsPerUnit * units);
  }

  private boolean isCommercial() {
    final ShortTermRental str = (ShortTermRental) application.getExtension();
    if (str != null && str.getCommercial() != null) {
      return str.getCommercial();
    }
    // if commercial-flag is not available, assume false
    return false;
  }

  private void updatePriceByTimeAndArea(int priceInCents,
      ChronoUnit pricePeriod, int priceArea, boolean longTermDiscount) {
    final int numTimeUnits = amountOfStartingUnits(application.getStartTime(), application.getEndTime(), pricePeriod);

    final int numAreaUnits = (int) Math.ceil(applicationArea / priceArea);
    long price = numAreaUnits * numTimeUnits * priceInCents;
    if (longTermDiscount == true && numTimeUnits > LONG_TERM_DISCOUNT_LIMIT) {
      // 50% discount for extra days
      final int numDiscountUnits = numTimeUnits - LONG_TERM_DISCOUNT_LIMIT;
      price -= numAreaUnits * numDiscountUnits * priceInCents / 2;
    }
    setPriceInCents((int) price);
  }

  /*
   * Calculate how many time units (day, month, etc) start during given time
   * period.
   */
  private int amountOfStartingUnits(ZonedDateTime startTime, ZonedDateTime endTime, ChronoUnit chronoUnit) {
    if (startTime == null || endTime == null) {
      return 0;
    }
    // ChronoUnit.between returns the number of full time periods, so move the
    // end time almost one full unit forward.
    return (int) chronoUnit.between(startTime, endTime.plus(1, chronoUnit).minusSeconds(1));
  }

  private void updateUrbanFarmingPrice() {
    // 2 EUR/sqm/term
    int numTerms = 1;
    if (application.getEndTime() != null && application.getStartTime() != null) {
      numTerms = application.getEndTime().getYear() - application.getStartTime().getYear() + 1;
    }
    if (applicationArea != 0.0) {
      setPriceInCents(URBAN_FARMING_TERM_PRICE * (int) Math.ceil(applicationArea) * numTerms);
    } else {
      setPriceInCents(0);
    }
  }
}
