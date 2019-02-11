package fi.hel.allu.model.service;

import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.types.ChargeBasisType;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.ChargeBasisCalc;
import fi.hel.allu.model.testUtils.TestCommon;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class PricingServiceTest {

  @Autowired
  private ApplicationDao applicationDao;

  @Autowired
  private PricingService pricingService;

  @Autowired
  private LocationDao locationDao;

  @Autowired
  private CustomerDao customerDao;
  @Autowired
  TestCommon testCommon;

  private Map<Triple<ApplicationKind, String, String>, FixedLocation> knownFixedLocations;

  @Before
  public void setUp() throws Exception {
    knownFixedLocations = locationDao.getFixedLocationList(null, null).stream()
        .collect(Collectors
            .toMap(fl -> Triple.of(fl.getApplicationKind(), fl.getArea(), fl.getSection()), Function.identity()));
  }

  @Test
  public void testMultiSectionApplication() {
    // Create a one-day event with two build days and one teardown day,
    // EcoCompass discount and
    // fixed locations "Rautatientori, lohko A" and "Rautatientori, lohko C".
    // The expected price is (1 * (500 + 500) + 3 * (250 + 250)) * 0.7 EUR =
    // 1750.00 EUR
    Application application = new Application();
    application.setType(ApplicationType.EVENT);
    application.setStartTime(ZonedDateTime.parse("2018-03-28T00:00:00+03:00"));
    application.setEndTime(ZonedDateTime.parse("2018-03-31T23:59:59.999+03:00"));
    application.setRecurringEndTime(application.getEndTime());
    application.setMetadataVersion(1);
    Event event = new Event();
    event.setEcoCompass(true);
    event.setNature(EventNature.PUBLIC_FREE);
    event.setEventStartTime(ZonedDateTime.parse("2018-03-29T21:00:00Z")); // 24
                                                                          // hours
    event.setEventEndTime(ZonedDateTime.parse("2018-03-30T20:59:59.999Z"));
    application.setExtension(event);
    application
        .setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.OUTDOOREVENT, Collections.emptyList()));
    addDummyCustomer(application, CustomerType.PERSON);
    application.setNotBillable(false);
    application = applicationDao.insert(application);
    Location location = newLocationWithDefaults();
    List<Integer> fixedLocationIds = Arrays
        .asList(Triple.of(ApplicationKind.OUTDOOREVENT, "Rautatientori", "A"),
            Triple.of(ApplicationKind.OUTDOOREVENT, "Rautatientori", "C"))
        .stream()
        .map(pair -> knownFixedLocations.get(pair).getId()).collect(Collectors.toList());
    location.setFixedLocationIds(fixedLocationIds);
    location.setApplicationId(application.getId());
    locationDao.insert(location);
    List<ChargeBasisEntry> chargeBasisEntries = pricingService.calculateChargeBasis(application);
    // Make sure there is one event day...
    assertEquals(1.0, chargeBasisEntries.stream().filter(cbe -> cbe.getUnitPrice() == 50000)
        .map(cbe -> cbe.getQuantity()).findFirst().orElse(0.0).doubleValue(), 0.0001);
    // ...and three build/teardown days
    assertEquals(3.0, chargeBasisEntries.stream().filter(cbe -> cbe.getUnitPrice() == 25000)
        .map(cbe -> cbe.getQuantity()).findFirst().orElse(0.0).doubleValue(), 0.0001);
    assertEquals(175000, pricingService.totalPrice(chargeBasisEntries));
    checkPrice(application, 175000);
  }

  @Test
  public void testPromotionEvent() {
    // fixed locations "Rautatientori, lohko A" and "Rautatientori, lohko C".
    // The expected price is (4 * (500 + 500) + 1 * (250 + 250)) =
    // 4500.00 EUR
    Application application = new Application();
    application.setType(ApplicationType.EVENT);
    application.setStartTime(ZonedDateTime.parse("2018-05-03T00:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2018-05-07T23:59:59+02:00"));
    application.setRecurringEndTime(application.getEndTime());
    application.setMetadataVersion(1);
    Event event = new Event();
    event.setNature(EventNature.PROMOTION);
    event.setEventStartTime(application.getStartTime().plusHours(24)); // 24 hours
    event.setEventEndTime(application.getEndTime());
    application.setExtension(event);
    application.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.PROMOTION, Collections.emptyList()));
    addDummyCustomer(application, CustomerType.PERSON);
    application.setNotBillable(false);
    application = applicationDao.insert(application);
    Location location = newLocationWithDefaults();
    List<Integer> fixedLocationIds = Arrays
        .asList(Triple.of(ApplicationKind.PROMOTION, "Rautatientori", "A"),
            Triple.of(ApplicationKind.PROMOTION, "Rautatientori", "C"))
        .stream().map(pair -> knownFixedLocations.get(pair).getId()).collect(Collectors.toList());
    location.setFixedLocationIds(fixedLocationIds);
    location.setApplicationId(application.getId());
    locationDao.insert(location);
    List<ChargeBasisEntry> chargeBasisEntries = pricingService.calculateChargeBasis(application);
    assertEquals(450000, pricingService.totalPrice(chargeBasisEntries));
    checkPrice(application, 450000);
  }

  @Test
  public void testBridgeBanner() {
    Application application = new Application();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    ShortTermRental event = new ShortTermRental();
    event.setCommercial(false);
    application.setStartTime(ZonedDateTime.parse("2016-11-07T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-12-10T23:00:00+02:00"));
    application.setExtension(event);
    application
        .setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.BRIDGE_BANNER, Collections.emptyList()));
    addDummyCustomer(application, CustomerType.PERSON);
    // Five weeks non-commercial -> 750 EUR
    checkPrice(application, 75000);

    event.setCommercial(true);
    application.setStartTime(ZonedDateTime.parse("2016-11-14T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-11-28T05:59:59+02:00"));
    // Three calendar week commercial -> 2250 EUR
    checkPrice(application, 225000);
    // Make sure "skip price calculation" is respected:
    application.setSkipPriceCalculation(true);
    checkPrice(application, 0);
  }

  @Test
  public void testCircus() {
    Application application = new Application();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setExtension(new ShortTermRental());
    application
        .setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.CIRCUS, Collections.emptyList()));
    application.setStartTime(ZonedDateTime.parse("2016-11-07T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-12-10T05:59:59+02:00"));
    addDummyCustomer(application, CustomerType.PERSON);
    // Thirty-four days -> 6800 EUR
    checkPrice(application, 680000);
  }

  @Test
  public void testDogTrainingEvent() {
    Application application = new Application();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setExtension(new ShortTermRental());
    application
        .setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.DOG_TRAINING_EVENT, Collections.emptyList()));
    application.setStartTime(ZonedDateTime.parse("2016-11-07T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-12-10T05:59:59+02:00"));
    addDummyCustomer(application, CustomerType.ASSOCIATION);
    // association -> 50 EUR /applicationExtension
    checkPrice(application, 5000);

    addDummyCustomer(application, CustomerType.COMPANY);
    // Company -> 100 EUR /applicationExtension
    checkPrice(application, 10000);
  }

  @Test
  public void testDogTrainingField() {
    Application application = new Application();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setExtension(new ShortTermRental());
    application
        .setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.DOG_TRAINING_FIELD, Collections.emptyList()));
    application.setStartTime(ZonedDateTime.parse("2016-11-07T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2018-12-10T05:59:59+02:00"));
    addDummyCustomer(application, CustomerType.PERSON);
    // association -> 100 EUR /year -> 300 EUR total
    checkPrice(application, 30000);

    addDummyCustomer(application, CustomerType.COMPANY);
    // Company -> 300 EUR /year -> 900 EUR total
    checkPrice(application, 90000);
  }

  @Test
  public void testKeskuskatuSales() {
    Application application = new Application();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setStartTime(ZonedDateTime.parse("2016-12-03T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-12-22T05:59:59+02:00"));
    application.setRecurringEndTime(application.getEndTime());
    application.setMetadataVersion(1);
    application.setExtension(new ShortTermRental());
    application
        .setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.KESKUSKATU_SALES, Collections.emptyList()));
    addDummyCustomer(application, CustomerType.PERSON);
    application.setNotBillable(false);
    application = applicationDao.insert(application);
    Location location = newLocationWithDefaults();
    location.setAreaOverride(135.5);
    location.setApplicationId(application.getId());
    locationDao.insert(location).getId();
    // 20 days, 135.5 sqm -> 20 * 14 * 50 + 6 * 14 * 25 = 14000 EUR
    checkPrice(application, 1400000);
  }

  @Test
  public void testPromotionOrSales() {
    Application application = new Application();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setStartTime(ZonedDateTime.parse("2016-12-03T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2018-12-22T05:59:59+02:00"));
    application.setRecurringEndTime(application.getEndTime());
    application.setMetadataVersion(1);
    ShortTermRental event = new ShortTermRental();
    event.setBillableSalesArea(true);
    application.setExtension(event);
    application
        .setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.PROMOTION_OR_SALES, Collections.emptyList()));
    application.setNotBillable(false);
    addDummyCustomer(application, CustomerType.PERSON);
    application = applicationDao.insert(application);
    Location location = newLocationWithDefaults();
    location.setAreaOverride(4.0);
    location.setApplicationId(application.getId());
    locationDao.insert(location).getId();
    // Large area, price for 25 months & 4 sqm = 25*2*4 EUR = 200 EUR
    checkPrice(application, 20000);

    // Small area is free
    ((ShortTermRental)application.getExtension()).setBillableSalesArea(false);
    application = applicationDao.update(application.getId(), application);
    checkPrice(application, 0);
  }

  @Test
  public void testSummerTheatre() {
    Application application = new Application();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setStartTime(ZonedDateTime.parse("2017-06-15T08:30:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2017-08-10T23:59:59+02:00"));
    application.setExtension(new ShortTermRental());
    application
        .setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.SUMMER_THEATER, Collections.emptyList()));
    application.setMetadataVersion(1);
    addDummyCustomer(application, CustomerType.PERSON);
    // Three months -> 360 EUR
    checkPrice(application, 36000);
  }

  @Test
  public void testUrbanFarming() {
    Application application = new Application();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setStartTime(ZonedDateTime.parse("2017-05-15T08:30:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2019-09-10T23:59:59+02:00"));
    application.setRecurringEndTime(application.getEndTime());
    application.setExtension(new ShortTermRental());
    application
        .setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.URBAN_FARMING, Collections.emptyList()));
    application.setMetadataVersion(1);
    application.setNotBillable(false);
    addDummyCustomer(application, CustomerType.PERSON);
    application = applicationDao.insert(application);
    Location location = newLocationWithDefaults();
    location.setAreaOverride(222.2);
    location.setApplicationId(application.getId());
    locationDao.insert(location).getId();
    // Three terms, 222.2 sqm -> 223 * 2 * 3 = 1338 EUR
    checkPrice(application, 133800);
  }

  // Invoincing-related test data:
  private static final ChargeBasisEntry[] TEST_ENTRIES = new ChargeBasisEntry[] {
      new ChargeBasisEntry("TAG1", null, false, ChargeBasisType.CALCULATED, ChargeBasisUnit.PIECE, 5.0, "Entry 1",
          new String[] { "One entry", "Item" }, 1230, 6150),
      new ChargeBasisEntry("TAG2", null, false, ChargeBasisType.CALCULATED, ChargeBasisUnit.DAY, 3.0, "Entry 2",
          new String[] { "Other entry", "Second item" }, 22000, 66000),
      new ChargeBasisEntry("TAG3", null, false, ChargeBasisType.CALCULATED, ChargeBasisUnit.SQUARE_METER, 123.5, "Entry 3", null, 200, 24700),
      new ChargeBasisEntry(null, "TAG1", true, ChargeBasisType.DISCOUNT, ChargeBasisUnit.PERCENT, -20.0, "20% discount",
          null, 0, 0),
      new ChargeBasisEntry(null, "TAG1", true, ChargeBasisType.DISCOUNT, ChargeBasisUnit.PERCENT, -10.0, "10% discount",
          null, 0, 0),
      new ChargeBasisEntry(null, "TAG2", true, ChargeBasisType.ADDITIONAL_FEE, ChargeBasisUnit.PERCENT, 20.0,
          "20% extra fee", null, 0, 0),
      new ChargeBasisEntry(null, null, true, ChargeBasisType.DISCOUNT, ChargeBasisUnit.PERCENT, -10.0, "10% discount",
          null, 0, 0)
  };

  // The total price should be
  // (61.50 * 0.8 * 0.9 + 660.00 * 1.2 + 247.00) * 0.9 = 974.95 EUR
  private static final int TEST_ENTRIES_PRICE = 97495;

  @Test
  public void testTotalPrice() {
    List<ChargeBasisEntry> entries = Arrays.asList(TEST_ENTRIES);
    assertEquals(TEST_ENTRIES_PRICE, pricingService.totalPrice(entries));
  }

  @Test
  public void testSingleInvoice()
  {
    List<ChargeBasisEntry> entries = Arrays.asList(TEST_ENTRIES);
    // The total price should be
    // (61.50 * 0.8 * 0.9 + 660.00 * 1.2 + 247.00) * 0.9 = 974.95 EUR
    List<InvoiceRow> rows = pricingService.toSingleInvoice(entries);
    assertFalse(rows.isEmpty());
    assertEquals(TEST_ENTRIES_PRICE, rows.stream().mapToInt(r -> r.getNetPrice()).sum());
  }

  /*
   * Verify that the sum of invoice lines matches the application's calculated
   * price
   */
  private void checkPrice(Application application, int expectedPrice) {
    List<ChargeBasisEntry> chargeBasisEntries = pricingService.calculateChargeBasis(application);

    assertEquals(expectedPrice, pricingService.totalPrice(chargeBasisEntries));
    ChargeBasisCalc cbc = new ChargeBasisCalc(chargeBasisEntries);
    List<InvoiceRow> invoiceRows = cbc.toInvoiceRows();
    int invoiced = invoiceRows.stream().mapToInt(entry -> entry.getNetPrice()).sum();
    assertEquals(expectedPrice, invoiced);
    invoiceRows.stream().forEach(r -> {
      double error = Math.abs(r.getNetPrice() - r.getUnitPrice() * r.getQuantity());
      assertTrue(error < 0.00001);
    });
  }

 private Location newLocationWithDefaults() {
   Location location = new Location();
   location.setUnderpass(false);
   location.setStartTime(ZonedDateTime.now());
   location.setEndTime(ZonedDateTime.now());
   return location;
 }

 private void addDummyCustomer(Application application, CustomerType customerType) {
   Customer customer = new Customer();
   customer.setName("Hakija");
   customer.setType(customerType);
   customer.setCountryId(testCommon.getCountryIdOfFinland());
   application.setCustomersWithContacts(
       Collections.singletonList(new CustomerWithContacts(CustomerRoleType.APPLICANT, customerDao.insert(customer), Collections.emptyList())));
 }
}
