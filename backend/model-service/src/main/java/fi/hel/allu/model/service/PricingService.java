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

import org.apache.commons.lang3.NotImplementedException;
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
   * Calculate the event price for the application
   *
   * @param application
   *          The application for which the pricing is calculated
   * @return price in cents
   */
  @Transactional(readOnly = true)
  public int calculatePrice(Application application) {
    OutdoorEvent outdoorEvent = (OutdoorEvent) application.getEvent();
    if (outdoorEvent != null) {
      return calculatePrice(application, outdoorEvent);
    }
    throw new NotImplementedException("Pricing calculation not implemented for this application type.");
  }

  // Pricing calculation for OutdoorEvent applications
  private int calculatePrice(Application application, OutdoorEvent outdoorEvent) {
    Optional<Location> location = LocationDao.findById(application.getLocationId());
    if (location.isPresent() == false) {
      throw new NoSuchEntityException("Location (ID=" + application.getLocationId() + " doesn't exist");
    }

    int squareSectionId = location.get().getSquareSectionId();
    OutdoorEventNature nature = outdoorEvent.getNature();
    Optional<PricingConfiguration> pricingConfiguration =
        pricingDao.findBySquareSectionAndNature(squareSectionId, nature);
    if (pricingConfiguration.isPresent() == false) {
      throw new NoSuchEntityException("No pricing configuration for (" + squareSectionId + nature + ")");
    }

    Pricing pricing = new Pricing();
    int eventDays = daysBetween(outdoorEvent.getEventStartTime(), outdoorEvent.getEventEndTime());
    int buildDays = daysBetween(application.getStartTime(), outdoorEvent.getEventStartTime());
    buildDays += daysBetween(outdoorEvent.getEventEndTime(), application.getEndTime());
    double structureArea = outdoorEvent.getStructureArea();
    double area = location.get().getArea();
    // TODO: pass in all the discount-related fields
    long hundrethsOfCents = pricing.calculateFullPrice(pricingConfiguration.get(), eventDays, buildDays, structureArea,
        area);
    return (int) ((hundrethsOfCents + 50) / 100);
  }

  // Calculate amount of days between two timestamps, ignoring the hours.
  private int daysBetween(ZonedDateTime startTime, ZonedDateTime endTime) {
    return (int) startTime.truncatedTo(ChronoUnit.DAYS).until(endTime.truncatedTo(ChronoUnit.DAYS),
        ChronoUnit.DAYS);
  }
}
