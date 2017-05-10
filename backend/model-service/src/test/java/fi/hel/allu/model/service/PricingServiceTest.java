package fi.hel.allu.model.service;

import fi.hel.allu.common.types.ApplicantType;
import fi.hel.allu.common.types.ApplicationKind;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.ApplicantDao;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
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
  private ApplicantDao applicantDao;

  @SuppressWarnings("serial")
  private static class Pair<A, B> extends AbstractMap.SimpleImmutableEntry<A, B> {
    public Pair(A key, B value) {
      super(key, value);
    }
  }

  private Map<Pair<String, String>, FixedLocation> knownFixedLocations;

  private static <A, B> Pair<A, B> makePair(A a, B b) {
    return new Pair<>(a, b);
  }

  @Before
  public void setUp() throws Exception {
    knownFixedLocations = locationDao.getFixedLocationList().stream()
        .filter(fl -> fl.getApplicationKind() == ApplicationKind.OUTDOOREVENT)
        .collect(Collectors
        .toMap(fl -> makePair(fl.getArea(), fl.getSection()), Function.identity()));
  }

  @Test
  public void testMultiSectionApplication() {
    // Create a five day application with one build day, EcoCompass discount and
    // fixed locations "Kansalaistori, lohko A" and "Kansalaistori, lohko C".
    // The expected price is (4 * (500 + 400) + 1 * (250 + 200)) * 0.7 EUR =
    // 2835.00 EUR
    Application application = new Application();
    application.setType(ApplicationType.EVENT);
    application.setKind(ApplicationKind.OUTDOOREVENT);
    application.setStartTime(ZonedDateTime.parse("2016-12-03T09:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-12-07T09:00:00+02:00"));
    application.setRecurringEndTime(application.getEndTime());
    application.setMetadataVersion(1);
    Event event = new Event();
    event.setEcoCompass(true);
    event.setNature(EventNature.PUBLIC_FREE);
    event.setBuildSeconds(60 * 60 * 24); // 24 hours
    application.setExtension(event);
    application = applicationDao.insert(application);
    Location location = newLocationWithDefaults();
    List<Integer> fixedLocationIds = Arrays.asList(makePair("Kansalaistori", "A"), makePair("Kansalaistori", "C"))
        .stream()
        .map(pair -> knownFixedLocations.get(pair).getId()).collect(Collectors.toList());
    location.setFixedLocationIds(fixedLocationIds);
    location.setApplicationId(application.getId());
    locationDao.insert(location);
    List<InvoiceRow> invoiceRows = new ArrayList<>();
    pricingService.updatePrice(application, invoiceRows);
    assertEquals(283500, application.getCalculatedPrice().intValue());
    checkPrice(application, 283500);
  }

  @Test
  public void testBridgeBanner() {
    Application application = new Application();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setKind(ApplicationKind.BRIDGE_BANNER);
    ShortTermRental event = new ShortTermRental();
    event.setCommercial(false);
    application.setStartTime(ZonedDateTime.parse("2016-11-07T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-12-10T23:00:00+02:00"));
    application.setExtension(event);
    // Five weeks non-commercial -> 750 EUR
    checkPrice(application, 75000);

    event.setCommercial(true);
    application.setStartTime(ZonedDateTime.parse("2016-11-14T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-11-28T05:59:59+02:00"));
    // Three calendar week commercial -> 2250 EUR
    checkPrice(application, 225000);
  }

  @Test
  public void testCircus() {
    Application application = new Application();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setKind(ApplicationKind.CIRCUS);
    application.setStartTime(ZonedDateTime.parse("2016-11-07T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-12-10T05:59:59+02:00"));
    // Thirty-four days -> 6800 EUR
    checkPrice(application, 680000);
  }

  @Test
  public void testDogTrainingEvent() {
    Application application = new Application();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setKind(ApplicationKind.DOG_TRAINING_EVENT);
    application.setStartTime(ZonedDateTime.parse("2016-11-07T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-12-10T05:59:59+02:00"));
    Applicant applicant = new Applicant();
    applicant.setName("Hakija");
    applicant.setType(ApplicantType.ASSOCIATION);
    application.setApplicantId(applicantDao.insert(applicant).getId());
    // association -> 50 EUR /applicationExtension
    checkPrice(application, 5000);

    applicant.setType(ApplicantType.COMPANY);
    application.setApplicantId(applicantDao.insert(applicant).getId());
    // Company -> 100 EUR /applicationExtension
    checkPrice(application, 10000);
  }

  @Test
  public void testDogTrainingField() {
    Application application = new Application();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setKind(ApplicationKind.DOG_TRAINING_FIELD);
    application.setStartTime(ZonedDateTime.parse("2016-11-07T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2018-12-10T05:59:59+02:00"));
    Applicant applicant = new Applicant();
    applicant.setName("Hakija");
    applicant.setType(ApplicantType.ASSOCIATION);
    application.setApplicantId(applicantDao.insert(applicant).getId());
    // association -> 100 EUR /year -> 300 EUR total
    checkPrice(application, 30000);

    applicant.setType(ApplicantType.COMPANY);
    application.setApplicantId(applicantDao.insert(applicant).getId());
    // Company -> 200 EUR /year -> 600 EUR total
    checkPrice(application, 60000);
  }

  @Test
  public void testKeskuskatuSales() {
    Application application = new Application();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setKind(ApplicationKind.KESKUSKATU_SALES);
    application.setStartTime(ZonedDateTime.parse("2016-12-03T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-12-22T05:59:59+02:00"));
    application.setRecurringEndTime(application.getEndTime());
    application.setMetadataVersion(1);
    application.setExtension(new ShortTermRental());
    application = applicationDao.insert(application);
    Location location = newLocationWithDefaults();
    location.setAreaOverride(135.5);
    location.setApplicationId(application.getId());
    locationDao.insert(location).getId();
    // 20 days, 135.5 sqm -> 14 * 14 * 50 + 6 * 14 * 25 = 11900 EUR
    checkPrice(application, 1190000);
  }

  @Test
  public void testPromotionOrSales() {
    Application application = new Application();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setKind(ApplicationKind.PROMOTION_OR_SALES);
    ShortTermRental event = new ShortTermRental();
    event.setLargeSalesArea(true);
    application.setExtension(event);
    application.setStartTime(ZonedDateTime.parse("2016-12-03T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2018-12-22T05:59:59+02:00"));
    // Large area, price for three years = 150 EUR * 3 = 450 EUR
    checkPrice(application, 45000);

    // Small area is free
    event.setLargeSalesArea(false);
    checkPrice(application, 0);
  }

  @Test
  public void testSummerTheatre() {
    Application application = new Application();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setKind(ApplicationKind.SUMMER_THEATER);
    application.setStartTime(ZonedDateTime.parse("2017-06-15T08:30:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2017-08-10T23:59:59+02:00"));
    application.setExtension(new ShortTermRental());
    application.setMetadataVersion(1);
    // Three months -> 360 EUR
    checkPrice(application, 36000);
  }

  @Test
  public void testUrbanFarming() {
    Application application = new Application();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setKind(ApplicationKind.URBAN_FARMING);
    application.setStartTime(ZonedDateTime.parse("2017-05-15T08:30:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2019-09-10T23:59:59+02:00"));
    application.setRecurringEndTime(application.getEndTime());
    application.setExtension(new ShortTermRental());
    application.setMetadataVersion(1);
    application = applicationDao.insert(application);
    Location location = newLocationWithDefaults();
    location.setAreaOverride(222.2);
    location.setApplicationId(application.getId());
    locationDao.insert(location).getId();
    // Three terms, 222.2 sqm -> 223 * 2 * 3 = 1338 EUR
    checkPrice(application, 133800);
  }

  /*
   * Verify that the sum of invoice lines matches the application's calculated
   * price
   */
  private void checkPrice(Application application, int expectedPrice) {
    List<InvoiceRow> invoiceRows = new ArrayList<>();

    pricingService.updatePrice(application, invoiceRows);

    assertEquals(expectedPrice, application.getCalculatedPrice().intValue());
    int invoiced = invoiceRows.stream().mapToInt(row -> row.getNetPrice()).sum();
    assertEquals(application.getCalculatedPrice().intValue(), invoiced);
    for (InvoiceRow invoiceRow : invoiceRows) {
      double error = Math.abs(invoiceRow.getNetPrice() - invoiceRow.getUnitPrice() * invoiceRow.getQuantity());
      assertTrue(error < 0.00001);
    }
  }

 private Location newLocationWithDefaults() {
   Location location = new Location();
   location.setUnderpass(false);
   location.setStartTime(ZonedDateTime.now());
   location.setEndTime(ZonedDateTime.now());
   return location;
 }
}

