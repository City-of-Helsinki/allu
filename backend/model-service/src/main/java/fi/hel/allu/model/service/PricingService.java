package fi.hel.allu.model.service;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.common.domain.types.SurfaceHardness;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.dao.TerminationDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.util.EventDayUtil;
import fi.hel.allu.model.domain.util.PriceUtil;
import fi.hel.allu.model.domain.util.Printable;
import fi.hel.allu.model.pricing.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

  private final PricingDao pricingDao;
  private final LocationDao locationDao;
  private final CustomerDao customerDao;
  private final PricingExplanator pricingExplanator;
  private final WinterTimeService winterTimeService;
  private final InvoicingPeriodService invoicingPeriodService;
  private final TerminationDao terminationDao;

  private static final Location EMPTY_LOCATION;

  static {
    EMPTY_LOCATION = new Location();
    EMPTY_LOCATION.setArea(0.0);
  }

  @Autowired
  public PricingService(PricingDao pricingDao, LocationDao locationDao, CustomerDao customerDao, WinterTimeService winterTimeService,
      InvoicingPeriodService invoicingPeriodService, TerminationDao terminationDao) {
    this.pricingDao = pricingDao;
    this.locationDao = locationDao;
    this.customerDao = customerDao;
    this.pricingExplanator = new PricingExplanator(locationDao);
    this.winterTimeService = winterTimeService;
    this.invoicingPeriodService = invoicingPeriodService;
    this.terminationDao = terminationDao;
  }

  public List<String> getPaymentClasses(ApplicationType applicationType, ApplicationKind applicationKind) {
    return pricingDao.getPaymentClasses(applicationType, applicationKind);
  }

  /**
   * Calculate basis for application pricing
   *
   * @param application The application for which the pricing is calculated.
   * @return List containing charge basis for application
   */
  @Transactional
  public List<ChargeBasisEntry> calculateChargeBasis(Application application) {
    return calculateChargeBasis(application, true);
  }

  @Transactional
  public List<ChargeBasisEntry> calculateChargeBasisWithoutInvoicingPeriods(Application application) {
    return calculateChargeBasis(application, false);
  }

  private List<ChargeBasisEntry> calculateChargeBasis(Application application, boolean periods) {
    if (application.getSkipPriceCalculation() == true) {
      return Collections.emptyList();
    } else if (application.getType() == ApplicationType.SHORT_TERM_RENTAL) {
      return updateShortTermRentalPrice(application);
    } else if (application.getType() == ApplicationType.EVENT) {
      return updateEventPrice(application);
    } else if (application.getType() == ApplicationType.EXCAVATION_ANNOUNCEMENT) {
      return updateExcavationAnnouncementPrice(application);
    } else if (application.getType() == ApplicationType.AREA_RENTAL) {
      return updateAreaRentalPrice(application, periods);
    } else {
      return Collections.emptyList();
    }
  }


  /**
   * Calculate the total price of given charge basis entries
   *
   * @param chargeBasisEntries
   * @return total price in cents
   */
  public int totalPrice(List<ChargeBasisEntry> chargeBasisEntries) {
    List<ChargeBasisEntry> invoicable = chargeBasisEntries.stream()
        .filter(entry -> entry.isInvoicable())
        .collect(Collectors.toList());
    return PriceUtil.totalPrice(invoicable);
  }

  /**
   * Generate a single (i.e., non-repeating) invoice from the given charge basis
   * entries.
   *
   * @param entries charge basis entries to use
   * @return resulting invoice rows
   */
  public List<InvoiceRow> toSingleInvoice(List<ChargeBasisEntry> entries) {
    return new ChargeBasisCalc(entries).toInvoiceRows();
  }

  /*
   * Calculate price for event application
   */
  private List<ChargeBasisEntry> updateEventPrice(Application application) {
    Event event = (Event) application.getExtension();
    // check that application is not new
    if (application.getId() != null && event != null) {
      EventPricing pricing = new EventPricing();
      calculateEventPrice(application, event, pricing);
      // pass the charge basis entries to caller
      return pricing.getChargeBasisEntries();
    } else {
      return Collections.emptyList();
    }
  }

  /*
   * Calculate price for short term rental application
   */
  private List<ChargeBasisEntry> updateShortTermRentalPrice(Application application) {
    List<Location> locations = Collections.emptyList();
    if (application.getId() != null) {
      locations = locationDao.findByApplicationId(application.getId());
    }
    double applicationArea = locations.stream().mapToDouble(l -> l.getEffectiveArea()).sum();
    ShortTermRentalPricing pricing = new ShortTermRentalPricing(application, pricingExplanator, pricingDao, terminationDao,
      applicationArea, isCompany(application), invoicingPeriodService.findForApplicationId(application.getId()), locations);
    pricing.calculatePrice();
    return pricing.getChargeBasisEntries();
  }

  /*
   * Calculate price for area rental
   */
  private List<ChargeBasisEntry> updateAreaRentalPrice(Application application, boolean periods) {
    return calculateChargeBasis(application,
        new AreaRentalPricing(application, pricingDao, pricingExplanator,
            periods ? invoicingPeriodService.findForApplicationId(application.getId()) : Collections.EMPTY_LIST));
  }

  /*
   * Calculate price for excavation announcement
   */
  private List<ChargeBasisEntry> updateExcavationAnnouncementPrice(Application application) {
    return calculateChargeBasis(application, new ExcavationPricing(
        application, winterTimeService, pricingExplanator, pricingDao, invoicingPeriodService.findForApplicationId(application.getId())));
  }

  /*
   * Calculate price using the common addLocationPrice
   */
  private  List<ChargeBasisEntry> calculateChargeBasis(Application application, Pricing pricing) {
    List<Location> locations = Collections.emptyList();
    if (application.getId() != null) {
      locations = locationDao.findByApplicationId(application.getId());
    }
    for (Location l : locations) {
      pricing.addLocationPrice(l, application.getStartTime());
    }
    return pricing.getChargeBasisEntries();
  }

  /*
   * EventPricing calculation for Event applications
   */
  private int calculateEventPrice(Application application, Event event, EventPricing pricing) {
    List<Location> locations = locationDao.findByApplicationId(application.getId());
    if (locations.size() > 1) {
      throw new RuntimeException("Event application has more than one location, which is the maximum");
    } else if (locations.isEmpty()) {
      return 0; // No location -> no price.
    }
    Location location = locations.get(0);
    EventNature nature = event.getNature();
    if (nature == null) {
      if (application.hasTypeAndKind(ApplicationType.EVENT, ApplicationKind.PROMOTION)) {
        // Promotion application has promotion nature by default.
        nature = EventNature.PROMOTION;
      } else {
        return 0; // No nature defined -> no price
      }
    }

    List<OutdoorPricingConfiguration> pricingConfigs = getEventPricing(location, nature, event.getSurfaceHardness());

    final int eventDays = EventDayUtil.eventDays(event.getEventStartTime(), event.getEventEndTime());
    final int buildDays = EventDayUtil.buildDays(event.getEventStartTime(), event.getEventEndTime(),
        application.getStartTime(), application.getEndTime());

    InfoTexts infoTexts = new InfoTexts();
    infoTexts.locationAddress = Printable.forPostalAddress(location.getPostalAddress());
    infoTexts.eventPeriod = Printable.forDayPeriod(event.getEventStartTime(), event.getEventEndTime());
    infoTexts.buildPeriods = buildDayPeriod(application.getStartTime(), event.getEventStartTime(),
        event.getEventEndTime(), application.getEndTime());

    for (OutdoorPricingConfiguration pricingConfig : pricingConfigs) {
      infoTexts.fixedLocation = Optional.ofNullable(pricingConfig.getFixedLocationId())
          .flatMap(id -> locationDao.findFixedLocation(id)).map(Printable::forFixedLocation).orElse(null);
      // Calculate price per location...
      pricing.accumulatePrice(pricingConfig, eventDays, buildDays, location.getEffectiveArea(), infoTexts, event.getNature());
    }
    // ... apply discounts...
    pricing.applyDiscounts(event.isEcoCompass());
    // ... and get the final price
    return pricing.getPriceInCents();
  }

  private String buildDayPeriod(ZonedDateTime buildStart, ZonedDateTime eventStart, ZonedDateTime eventEnd,
      ZonedDateTime teardownEnd) {
    List<String> periods = new ArrayList<>();
    if (buildStart != null && eventStart != null && ChronoUnit.DAYS.between(buildStart, eventStart) != 0) {
      periods.add(Printable.forDayPeriod(buildStart, eventStart.minusDays(1)));
    }
    if (teardownEnd != null && eventEnd != null && ChronoUnit.DAYS.between(eventEnd, teardownEnd) != 0) {
      periods.add(Printable.forDayPeriod(eventEnd.plusDays(1), teardownEnd));
    }
    return String.join("; ", periods);
  }
  /*
   * Get event pricings for given location and nature.
   * May return multiple pricings if location consists of fixed locations.
   */
  private List<OutdoorPricingConfiguration> getEventPricing(Location location, EventNature nature, SurfaceHardness surfaceHardness) {
    List<Integer> fixedLocationIds = location.getFixedLocationIds();
    List<OutdoorPricingConfiguration> pricingConfigurations = fixedLocationIds.stream()
        .map(f -> getPricingConfigurationForFixedLocation(f, location, nature, surfaceHardness))
        .filter(c -> c != null)
        .collect(Collectors.toList());

    if (CollectionUtils.isEmpty(pricingConfigurations)) {
      pricingConfigurations = Optional.ofNullable(getPricingConfigurationByDistrictAndNature(location, nature, surfaceHardness))
          .map(Collections::singletonList)
          .orElse(Collections.emptyList());
    }
    return pricingConfigurations;
  }

  private OutdoorPricingConfiguration getPricingConfigurationForFixedLocation(Integer fixedLocationId, Location location,
      EventNature nature, SurfaceHardness surfaceHardness) {
     Optional<OutdoorPricingConfiguration> configuration = pricingDao.findByFixedLocationAndNature(fixedLocationId, nature);
     return configuration.orElse(getPricingConfigurationByDistrictAndNature(location, nature, surfaceHardness));
  }

  private OutdoorPricingConfiguration getPricingConfigurationByDistrictAndNature(Location location,
      EventNature nature, SurfaceHardness surfaceHardness) {
    OutdoorPricingConfiguration configuration = null;
    if (location.getEffectiveCityDistrictId() != null && surfaceHardness != null) {
      configuration = pricingDao.findByDisctrictAndNature(location.getEffectiveCityDistrictId(), nature, surfaceHardness)
          .orElse(null);
    }
    return configuration;
  }

  private boolean isCompany(Application application) {
    if (application.getCustomersWithContacts().isEmpty()) {
      return false;
    }
    return customerDao.findByIds(application.getCustomersWithContacts().stream().map(cwc -> cwc.getCustomer().getId()).collect(Collectors.toList()))
        .stream().filter(a -> a.getType() == CustomerType.COMPANY).findFirst().isPresent();
  }

}
