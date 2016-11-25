package fi.hel.allu.model.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.ApplicantType;
import fi.hel.allu.common.types.OutdoorEventNature;
import fi.hel.allu.model.dao.ApplicantDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.OutdoorEvent;
import fi.hel.allu.model.domain.ShortTermRental;
import fi.hel.allu.model.pricing.Pricing;
import fi.hel.allu.model.pricing.PricingConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 *
 * The service class for price calculations
 *
 */
@Service
public class PricingService {

  private PricingDao pricingDao;
  private LocationDao locationDao;
  private ApplicantDao applicantDao;

  private static final Location EMPTY_LOCATION;

  // Various price constants for short term rental
  private static final int BRIDGE_BANNER_WEEKLY_PRICE_COMMERCIAL = 75000;   // 750 EUR/week
  private static final int BRIDGE_BANNER_WEEKLY_PRICE_NONCOMMERCIAL = 15000;   // 150 EUR/week
  private static final int CIRCUS_DAILY_PRICE = 20000;      // 200 EUR/day
  private static final int DOG_TRAINING_EVENT_ASSOCIATION_PRICE = 5000;   // 50 EUR
  private static final int DOG_TRAINING_EVENT_COMPANY_PRICE = 10000;   // 100 EUR
  private static final int DOG_TRAINING_FIELD_YEARLY_COMPANY = 20000;  // 200 EUR/year
  private static final int DOG_TRAINING_FIELD_YEARLY_ASSOCIATION = 10000; // 100 EUR/year
  private static final int KESKUSKATU_SALES_TEN_SQM_PRICE = 5000;  // 50 EUR/10sqm/day
  private static final int PROMOTION_OR_SALES_LARGE_YEARLY = 15000;  // 150 EUR/year
  private static final int SEASON_SALE_TEN_SQM_PRICE = 5000; // 50 EUR/10sqm/day
  private static final int SUMMER_THEATER_YEARLY_PRICE = 12000; // 120 EUR/year
  private static final int URBAN_FARMING_TERM_PRICE = 200; // 2.00 EUR/sqm/term
  private static final int LONG_TERM_DISCOUNT_LIMIT = 14; // how many days
                                                          // before discount?

  static {
    EMPTY_LOCATION = new Location();
    EMPTY_LOCATION.setArea(0.0);
  }

  @Autowired
  public PricingService(PricingDao pricingDao, LocationDao locationDao, ApplicantDao applicantDao) {
    this.pricingDao = pricingDao;
    this.locationDao = locationDao;
    this.applicantDao = applicantDao;
  }

  /**
   * Calculate and store the event price for the application
   *
   * @param application
   *          The application for which the pricing is calculated. New pricing
   *          is stored in the application.
   */
  @Transactional(readOnly = true)
  public void updatePrice(Application application) {
    switch (application.getType()) {
    case OUTDOOREVENT:
      updateOutdoorEventPrice(application);
      break;
    case ART:
      // Free event
      application.setCalculatedPrice(0);
      break;
    case SMALL_ART_AND_CULTURE:
      // Free event
      application.setCalculatedPrice(0);
      break;
    case BENJI:
      // Price not defined
      application.setCalculatedPrice(0);
      break;
    case BRIDGE_BANNER:
      // Non-commercial organizer: 150 EUR/week
      // Commercial organizer: 750 EUR/week
      if (isCommercial(application)) {
        updatePricePerUnit(application, ChronoUnit.WEEKS, BRIDGE_BANNER_WEEKLY_PRICE_COMMERCIAL);
      } else {
        updatePricePerUnit(application, ChronoUnit.WEEKS, BRIDGE_BANNER_WEEKLY_PRICE_NONCOMMERCIAL);
      }
      break;
    case CARGO_CONTAINER:
      // Price not defined
      application.setCalculatedPrice(0);
      break;
    case CIRCUS:
      updatePricePerUnit(application, ChronoUnit.DAYS, CIRCUS_DAILY_PRICE);
      break;
    case DOG_TRAINING_EVENT:
      // Associations: 50 EUR/event
      // Companies: 100 EUR/event
      if (isCompany(application)) {
        application.setCalculatedPrice(DOG_TRAINING_EVENT_COMPANY_PRICE);
      } else {
        application.setCalculatedPrice(DOG_TRAINING_EVENT_ASSOCIATION_PRICE);
      }
      break;
    case DOG_TRAINING_FIELD:
      // Associations: 100 EUR/year
      // Companies: 200 EUR/year
      if (isCompany(application)) {
        updatePricePerUnit(application, ChronoUnit.YEARS, DOG_TRAINING_FIELD_YEARLY_COMPANY);
      } else {
        updatePricePerUnit(application, ChronoUnit.YEARS, DOG_TRAINING_FIELD_YEARLY_ASSOCIATION);
      }
      break;
    case KESKUSKATU_SALES:
      // 1..14 days: 50 EUR/day/starting 10 sqm
      // 50% discount from 15. day onwards
      updatePriceByTimeAndArea(application, KESKUSKATU_SALES_TEN_SQM_PRICE, ChronoUnit.DAYS, 10, true);
      break;
    case OTHER_SHORT_TERM_RENTAL:
      // Unknown price
      application.setCalculatedPrice(0);
      break;
    case PROMOTION_OR_SALES:
      // 0.8 x 3.0 sqm: free of charge
      // bigger: 150 EUR/year
      ShortTermRental str = (ShortTermRental) application.getEvent();
      if (str != null && str.getLargeSalesArea() == true) {
        updatePricePerUnit(application, ChronoUnit.YEARS, PROMOTION_OR_SALES_LARGE_YEARLY);
      } else {
        application.setCalculatedPrice(0);
      }
      break;
    case SEASON_SALE:
      // 1..14 days: 50 EUR/day/starting 10 sqm
      // 50% discount from 15. day onwards
      updatePriceByTimeAndArea(application, SEASON_SALE_TEN_SQM_PRICE, ChronoUnit.DAYS, 10, true);
      break;
    case STORAGE_AREA:
      // Unknown price
      application.setCalculatedPrice(0);
      break;
    case SUMMER_THEATER:
      // 120 EUR/month
      updatePricePerUnit(application, ChronoUnit.MONTHS, SUMMER_THEATER_YEARLY_PRICE);
      break;
    case URBAN_FARMING:
      updateUrbanFarmingPrice(application);
      break;
    default:
      break;
    }
  }

  private void updatePriceByTimeAndArea(Application application, int priceInCents, ChronoUnit pricePeriod, int priceArea,
      boolean longTermDiscount) {
    int numTimeUnits = amountOfStartingUnits(application.getStartTime(), application.getEndTime(), pricePeriod);

    double applicationArea = getApplicationArea(application);
    int numAreaUnits = (int) Math.ceil(applicationArea / priceArea);
    long price = numAreaUnits * numTimeUnits * priceInCents;
    if (longTermDiscount == true && numTimeUnits > LONG_TERM_DISCOUNT_LIMIT) {
      // 50% discount for extra days
      int numDiscountUnits = numTimeUnits - LONG_TERM_DISCOUNT_LIMIT;
      price -= numAreaUnits * numDiscountUnits * priceInCents / 2;
    }
    application.setCalculatedPrice((int) price);
  }

  private double getApplicationArea(Application application) {
    if (application.getLocationId() == null) {
      return 0.0;
    }
    Location location = locationDao.findById(application.getLocationId()).orElse(EMPTY_LOCATION);

    Double area = Optional.ofNullable(location.getAreaOverride()).orElse(location.getArea());
    return area == null ? 0.0 : area.doubleValue();
  }

  // calculate how many time units (day, month, etc) start during given time
  // period
  private int amountOfStartingUnits(ZonedDateTime startTime, ZonedDateTime endTime, ChronoUnit chronoUnit) {
    if (startTime == null || endTime == null) {
      return 0;
    }
    // ChronoUnit.between returns the number of full time periods, so move the
    // end time almost one full unit forward.
    return (int) chronoUnit.between(startTime, endTime.plus(1, chronoUnit).minusSeconds(1));
  }

  private void updateUrbanFarmingPrice(Application application) {
    // 2 EUR/sqm/term
    int numTerms = 1;
    if (application.getEndTime() != null && application.getStartTime() != null) {
      numTerms = application.getEndTime().getYear() - application.getStartTime().getYear() + 1;
    }
    double area = getApplicationArea(application);
    if (area != 0.0) {
      application.setCalculatedPrice(URBAN_FARMING_TERM_PRICE * (int) Math.ceil(area) * numTerms);
    } else {
      application.setCalculatedPrice(0);
    }
  }

  private boolean isCompany(Application application) {
    return applicantDao.findById(application.getApplicantId()).filter(a -> a.getType() == ApplicantType.COMPANY)
        .isPresent();
  }

  // Calculate application's price using the application period
  // price is calculated as centsPerUnit * starting units and stored into
  // application
  private void updatePricePerUnit(Application application, ChronoUnit chronoUnit, int centsPerUnit) {
    int units = amountOfStartingUnits(application.getStartTime(), application.getEndTime(), chronoUnit);
    application.setCalculatedPrice(centsPerUnit * units);
  }

  private boolean isCommercial(Application application) {
    ShortTermRental str = (ShortTermRental) application.getEvent();
    if (str != null && str.getCommercial() != null) {
      return str.getCommercial();
    }
    // if commercial-flag is not available, assume false
    return false;
  }

  // Calculate price for outdoor event
  private void updateOutdoorEventPrice(Application application) {
    OutdoorEvent outdoorEvent = (OutdoorEvent) application.getEvent();
    if (outdoorEvent != null) {
      int priceInCents = calculatePrice(application, outdoorEvent);
      application.setCalculatedPrice(priceInCents);
    }
  }

  // Pricing calculation for OutdoorEvent applications
  private int calculatePrice(Application application, OutdoorEvent outdoorEvent) {
    Integer locationId = application.getLocationId();
    if (locationId == null) {
      return 0; // No location -> no price.
    }
    Optional<Location> location = locationDao.findById(locationId.intValue());
    if (location.isPresent() == false) {
      throw new NoSuchEntityException("Location (ID=" + application.getLocationId() + " doesn't exist");
    }
    OutdoorEventNature nature = outdoorEvent.getNature();
    if (nature == null) {
      return 0; // No nature defined -> no price
    }

    List<PricingConfiguration> pricingConfigs = Collections.emptyList();
    List<Integer> fixedLocationIds = location.get().getFixedLocationIds();
    if (fixedLocationIds != null && !fixedLocationIds.isEmpty()) {
      pricingConfigs = fixedLocationIds.stream()
        .map(flId -> pricingDao.findByFixedLocationAndNature(flId, nature))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
    } // TODO: pricing configuration for non-fixed locations (Zones)

    int eventDays = daysBetween(outdoorEvent.getEventStartTime(), outdoorEvent.getEventEndTime());
    int buildDays = daysBetween(application.getStartTime(), outdoorEvent.getEventStartTime());
    buildDays += daysBetween(outdoorEvent.getEventEndTime(), application.getEndTime());
    double structureArea = outdoorEvent.getStructureArea();
    double area = getApplicationArea(application);

    Pricing pricing = new Pricing();
    for(PricingConfiguration pricingConfig : pricingConfigs) {
      // Calculate price per location...
      pricing.accumulatePrice(pricingConfig, eventDays, buildDays, structureArea,
          area);
    }

    // ... apply discounts...
    pricing.applyDiscounts(outdoorEvent.isEcoCompass(), outdoorEvent.getNoPriceReason(),
        outdoorEvent.isHeavyStructure(), outdoorEvent.isSalesActivity());
    // ... and get the final price
    return pricing.getPrice();
  }


  // Calculate amount of days between two timestamps, ignoring the hours.
  private int daysBetween(ZonedDateTime startTime, ZonedDateTime endTime) {
    if (startTime == null || endTime == null)
      return 0;
    return (int) startTime.truncatedTo(ChronoUnit.DAYS).until(endTime.truncatedTo(ChronoUnit.DAYS),
        ChronoUnit.DAYS);
  }
}
