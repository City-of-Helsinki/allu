package fi.hel.allu.model.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.OutdoorEventNature;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ApplicationPricing;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.OutdoorEvent;
import fi.hel.allu.model.pricing.Pricing;
import fi.hel.allu.model.pricing.PricingConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
@Component
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
    // TODO: most likely if should be replaced by switch-case construct. Refactor when more application types are supported
    if (ApplicationType.OUTDOOREVENT.equals(application.getType())) {
      OutdoorEvent outdoorEvent = (OutdoorEvent) application.getEvent();
      if (outdoorEvent != null) {
        ApplicationPricing calculatedPricing = new ApplicationPricing();
        calculatedPricing.setPrice(calculatePrice(application, outdoorEvent));
        outdoorEvent.setCalculatedPricing(calculatedPricing);
      }
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
