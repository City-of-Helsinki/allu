package fi.hel.allu.model.service;

import fi.hel.allu.common.types.ApplicantType;
import fi.hel.allu.common.types.ApplicationKind;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.dao.ApplicantDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Event;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.pricing.EventPricing;
import fi.hel.allu.model.pricing.PricingConfiguration;
import fi.hel.allu.model.pricing.ShortTermRentalPricing;
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
   * @param invoiceRows
   *          List where to store the invoice rows from the price calculation.
   */
  @Transactional
  public void updatePrice(Application application, List<InvoiceRow> invoiceRows) {
    if (application.getType() == ApplicationType.SHORT_TERM_RENTAL) {
      updateShortTermRentalPrice(application, invoiceRows);
    } else if (application.getKind() == ApplicationKind.OUTDOOREVENT) {
      updateOutdoorEventPrice(application, invoiceRows);
    }
  }


  /*
   * Calculate price for outdoor event
   */
  private void updateOutdoorEventPrice(Application application, List<InvoiceRow> invoiceRows) {
    Event event = (Event) application.getExtension();
    // check that application is not new
    if (application.getId() != null && event != null) {
      EventPricing pricing = new EventPricing();
      int priceInCents = calculateEventPrice(application, event, pricing);
      application.setCalculatedPrice(priceInCents);
      // pass the invoice rows to caller
      invoiceRows.addAll(pricing.getInvoiceRows());
    }
  }

  /*
   * Calculate price for short term rental application
   */
  private void updateShortTermRentalPrice(Application application, List<InvoiceRow> invoiceRows) {
    List<Location> locations = Collections.emptyList();
    if (application.getId() != null) {
      locations = locationDao.findByApplication(application.getId());
    }
    // TODO: should we handle different locations as their own invoice rows? This works anyway as long as only area rentals have multiple locations
    double applicationArea = locations.stream().mapToDouble(l -> getLocationArea(l)).sum();
    ShortTermRentalPricing pricing = new ShortTermRentalPricing(application, applicationArea, isCompany(application));
    pricing.calculatePrice();
    application.setCalculatedPrice(pricing.getPriceInCents());
    invoiceRows.addAll(pricing.getInvoiceRows());
  }

  /*
   * EventPricing calculation for Event applications
   */
  private int calculateEventPrice(Application application, Event event, EventPricing pricing) {
    List<Location> locations = locationDao.findByApplication(application.getId());
    if (locations.size() > 1) {
      throw new RuntimeException("Event application has more than one location, which is the maximum");
    } else if (locations.isEmpty()) {
      return 0; // No location -> no price.
    }
    Location location = locations.get(0);

    EventNature nature = event.getNature();
    if (nature == null) {
      return 0; // No nature defined -> no price
    }

    List<PricingConfiguration> pricingConfigs = getEventPricing(location, nature);

    int eventDays = daysBetween(event.getEventStartTime(), event.getEventEndTime());
    int buildDays = daysBetween(application.getStartTime(), event.getEventStartTime());
    buildDays += daysBetween(event.getEventEndTime(), application.getEndTime());
    double structureArea = event.getStructureArea();
    double area = getLocationArea(location);

    for(PricingConfiguration pricingConfig : pricingConfigs) {
      // Calculate price per location...
      pricing.accumulatePrice(pricingConfig, eventDays, buildDays, structureArea,
          area);
    }

    // ... apply discounts...
    pricing.applyDiscounts(event.isEcoCompass(), event.getNoPriceReason(),
        event.isHeavyStructure(), event.isSalesActivity());
    // ... and get the final price
    return pricing.getPrice();
  }

  /*
   * Get event pricings for given location and nature.
   * May return multiple pricings if location consists of fixed locations.
   */
  private List<PricingConfiguration> getEventPricing(Location location, EventNature nature) {
    List<Integer> fixedLocationIds = location.getFixedLocationIds();
    if (fixedLocationIds != null && !fixedLocationIds.isEmpty()) {
      // fixed locations exist, so they define the pricing
      return fixedLocationIds.stream().map(flId -> pricingDao.findByFixedLocationAndNature(flId, nature))
          .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    } else {
      // Check if district is defined:
      Integer cityDistrictId = Optional.ofNullable(location.getCityDistrictIdOverride()).orElse(location.getCityDistrictId());
      if (cityDistrictId != null) {
        return pricingDao.findByDisctrictAndNature(cityDistrictId, nature).map(Collections::singletonList)
            .orElse(Collections.emptyList());
      }
    }
    // No pricing could be found:
    return Collections.emptyList();
  }

  private double getLocationArea(Location location) {
    return Optional.ofNullable(location.getAreaOverride()).orElse(location.getArea());
  }

  /*
   * Calculate amount of days between two timestamps, ignoring the hours.
   */
  private int daysBetween(ZonedDateTime startTime, ZonedDateTime endTime) {
    if (startTime == null || endTime == null)
      return 0;
    return (int) startTime.truncatedTo(ChronoUnit.DAYS).until(endTime.truncatedTo(ChronoUnit.DAYS),
        ChronoUnit.DAYS);
  }

  private boolean isCompany(Application application) {
    if (application.getApplicantId() == null) {
      return false;
    }
    return applicantDao.findById(application.getApplicantId()).filter(a -> a.getType() == ApplicantType.COMPANY)
        .isPresent();
  }
}
