package fi.hel.allu.model.service;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.pricing.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
  private CustomerDao customerDao;

  private static final Location EMPTY_LOCATION;

  static {
    EMPTY_LOCATION = new Location();
    EMPTY_LOCATION.setArea(0.0);
  }

  @Autowired
  public PricingService(PricingDao pricingDao, LocationDao locationDao, CustomerDao customerDao) {
    this.pricingDao = pricingDao;
    this.locationDao = locationDao;
    this.customerDao = customerDao;
  }

  /**
   * Calculate basis for application pricing
   *
   * @param application The application for which the pricing is calculated.
   * @return List containing charge basis for application
   */
  @Transactional
  public List<ChargeBasisEntry> calculateChargeBasis(Application application) {
    if (application.getType() == ApplicationType.SHORT_TERM_RENTAL) {
      return updateShortTermRentalPrice(application);
    } else if (application.hasTypeAndKind(ApplicationType.EVENT, ApplicationKind.OUTDOOREVENT)) {
      return updateOutdoorEventPrice(application);
    } else if (application.getType() == ApplicationType.EXCAVATION_ANNOUNCEMENT) {
      return updateExcavationAnnouncementPrice(application);
    } else if (application.getType() == ApplicationType.AREA_RENTAL) {
      return updateAreaRentalPrice(application);
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
    return new ChargeBasisCalc(chargeBasisEntries).toInvoiceRows().stream()
        .map(row -> BigDecimal.valueOf(row.getNetPrice()))
        .reduce((b1, b2) -> b1.add(b2)).orElse(BigDecimal.ZERO)
        .setScale(0, RoundingMode.UP).intValue();
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
   * Calculate price for outdoor event
   */
  private List<ChargeBasisEntry> updateOutdoorEventPrice(Application application) {
    Event event = (Event) application.getExtension();
    // check that application is not new
    if (application.getId() != null && event != null) {
      EventPricing pricing = new EventPricing();
      int priceInCents = calculateEventPrice(application, event, pricing);
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
      locations = locationDao.findByApplication(application.getId());
    }
    // TODO: should we handle different locations as their own charge basis
    // items? This works anyway as long as only area rentals have multiple
    // locations
    double applicationArea = locations.stream().mapToDouble(l -> l.getEffectiveArea()).sum();
    ShortTermRentalPricing pricing = new ShortTermRentalPricing(application, applicationArea, isCompany(application));
    pricing.calculatePrice();
    return pricing.getChargeBasisEntries();
  }

  /*
   * Calculate price for area rental
   */
  private List<ChargeBasisEntry> updateAreaRentalPrice(Application application) {
    return calculateChargeBasis(application, new AreaRentalPricing(application));
  }

  /*
   * Calculate price for excavation announcement
   */
  private List<ChargeBasisEntry> updateExcavationAnnouncementPrice(Application application) {
    return calculateChargeBasis(application, new ExcavationPricing(application));
  }

  /*
   * Calculate price using the common addLocationPrice
   */
  private  List<ChargeBasisEntry> calculateChargeBasis(Application application, Pricing pricing) {
    List<Location> locations = Collections.emptyList();
    if (application.getId() != null) {
      locations = locationDao.findByApplication(application.getId());
    }
    for (Location l : locations) {
      pricing.addLocationPrice(l.getLocationKey(), l.getEffectiveArea(), locationDao.getPaymentClass(l.getId()));
    }
    return pricing.getChargeBasisEntries();
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

    int reservationDays = (int) CalendarUtil.startingUnitsBetween(application.getStartTime(), application.getEndTime(),
        ChronoUnit.DAYS);
    int buildDays = (int) Math.round((double) event.getBuildSeconds() / (24 * 60 * 60));
    buildDays += (int) Math.round((double) event.getTeardownSeconds() / (24 * 60 * 60));
    int eventDays = reservationDays - buildDays;
    double structureArea = event.getStructureArea();
    double area = location.getEffectiveArea();

    for(PricingConfiguration pricingConfig : pricingConfigs) {
      // Calculate price per location...
      pricing.accumulatePrice(pricingConfig, eventDays, buildDays, structureArea,
          area);
    }

    // ... apply discounts...
    pricing.applyDiscounts(event.isEcoCompass(), application.getNotBillable(),
        event.isHeavyStructure(), event.isSalesActivity());
    // ... and get the final price
    return pricing.getPriceInCents();
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
      Integer cityDistrictId = location.getEffectiveCityDistrictId();
      if (cityDistrictId != null) {
        return pricingDao.findByDisctrictAndNature(cityDistrictId, nature).map(Collections::singletonList)
            .orElse(Collections.emptyList());
      }
    }
    // No pricing could be found:
    return Collections.emptyList();
  }


  private boolean isCompany(Application application) {
    if (application.getCustomersWithContacts().isEmpty()) {
      return false;
    }
    return customerDao.findByIds(application.getCustomersWithContacts().stream().map(cwc -> cwc.getCustomer().getId()).collect(Collectors.toList()))
        .stream().filter(a -> a.getType() == CustomerType.COMPANY).findFirst().isPresent();
  }

}
