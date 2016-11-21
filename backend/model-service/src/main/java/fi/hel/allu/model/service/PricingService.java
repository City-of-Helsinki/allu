package fi.hel.allu.model.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.OutdoorEventNature;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.OutdoorEvent;
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
  private LocationDao LocationDao;

  @Autowired
  public PricingService(PricingDao pricingDao, LocationDao locationDao) {
    this.pricingDao = pricingDao;
    this.LocationDao = locationDao;
  }

  /**
   * Calculate and store the event price for the application
   *
   * @param application
   *          The application for which the pricing is calculated. New pricing
   *          is stored in the application.
   */
  @Transactional(readOnly = true)
  public void calculatePrice(Application application) {
    switch (application.getType()) {
    case OUTDOOREVENT:
      calculateOutdoorEventPrice(application);
      break;
    case ART:
      // Free event
      break;
    case SMALL_ART_AND_CULTURE:
      // Free event
      break;
    case BENJI:
      // Unknown price
      break;
    case BRIDGE_BANNER:
      // Noncommercial organizer: 150 EUR/week
      // Commercial organizer: 750 EUR/week
      break;
    case CARGO_CONTAINER:
      // Unknown price
      break;
    case CIRCUS:
      // 200 EUR/day
      break;
    case DOG_TRAINING_EVENT:
      // Associations: 50 EUR/event
      // Companies: 100 EUR/event
      break;
    case DOG_TRAINING_FIELD:
      // Associations: 100 EUR/year
      // Companies: 200 EUR/year
      break;
    case KESKUSKATU_SALES:
      // 1..14 days: 50 EUR/day/starting 10 sqm
      // 50% discount from 15. day onwards
      break;
    case OTHER_SHORT_TERM_RENTAL:
      // Unknown price
      break;
    case PROMOTION_OR_SALES:
      // 0.8 x 3.0 sqm: free of charge
      // bigger: 150 EUR/year
      break;
    case SEASON_SALE:
      // 1..14 days: 50 EUR/day/starting 10 sqm
      // 50% discount from 15. day onwards
      break;
    case STORAGE_AREA:
      // Unknown price
      break;
    case SUMMER_THEATER:
      // 120 EUR/month
      break;
    case URBAN_FARMING:
      // 2 EUR/sqm/term
      break;
    default:
      break;
    }
  }

  // Calculate price for outdoor event
  private void calculateOutdoorEventPrice(Application application) {
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
    Optional<Location> location = LocationDao.findById(locationId.intValue());
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
    double area = Optional.ofNullable(location.get().getAreaOverride()).orElse(location.get().getArea());

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
