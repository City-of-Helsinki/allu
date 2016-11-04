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
import java.util.Optional;

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

    Integer fixedLocationId = location.get().getFixedLocationId();
    if (fixedLocationId == null) {
      return 0; // No area/section defined -> no price (TODO: infer proper
                // zone id)
    }
    OutdoorEventNature nature = outdoorEvent.getNature();
    if (nature == null) {
      return 0; // No nature defined -> no price
    }
    Optional<PricingConfiguration> pricingConfiguration =
        pricingDao.findByFixedLocationAndNature(fixedLocationId.intValue(), nature);
    if (pricingConfiguration.isPresent() == false) {
      throw new NoSuchEntityException("No pricing configuration for (" + fixedLocationId + nature + ")");
    }

    Pricing pricing = new Pricing();
    int eventDays = daysBetween(outdoorEvent.getEventStartTime(), outdoorEvent.getEventEndTime());
    int buildDays = daysBetween(application.getStartTime(), outdoorEvent.getEventStartTime());
    buildDays += daysBetween(outdoorEvent.getEventEndTime(), application.getEndTime());
    double structureArea = outdoorEvent.getStructureArea();
    double area = location.get().getArea();
    // Calculate full price...
    pricing.calculateFullPrice(pricingConfiguration.get(), eventDays, buildDays, structureArea,
        area);
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
