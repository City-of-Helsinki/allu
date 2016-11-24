package fi.hel.allu.model.service;

import fi.hel.allu.common.types.ApplicantType;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.OutdoorEventNature;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.ApplicantDao;
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
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class PricingServiceTest {

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
        .filter(fl -> fl.getApplicationType() == ApplicationType.OUTDOOREVENT)
        .collect(Collectors
        .toMap(fl -> makePair(fl.getArea(), fl.getSection()), Function.identity()));
  }

  @Test
  public void testMultiSectionApplication() {
    // Create a 4 day application with one build day, EcoCompass discount and
    // fixed locations "Kansalaistori, lohko A" and "Kansalaistori, lohko C".
    // The expected price is (3 * (500 + 400) + 1 * (250 + 200)) * 0.7 EUR =
    // 2205.00 EUR
    Application application = new Application();
    application.setType(ApplicationType.OUTDOOREVENT);
    application.setStartTime(ZonedDateTime.parse("2016-12-03T09:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-12-07T09:00:00+02:00"));
    OutdoorEvent event = new OutdoorEvent();
    event.setEcoCompass(true);
    event.setNature(OutdoorEventNature.PUBLIC_FREE);
    event.setEventStartTime(application.getStartTime().plusDays(1));
    event.setEventEndTime(application.getEndTime());
    application.setEvent(event);
    Location location = new Location();
    List<Integer> fixedLocationIds = Arrays.asList(makePair("Kansalaistori", "A"), makePair("Kansalaistori", "C"))
        .stream()
        .map(pair -> knownFixedLocations.get(pair).getId()).collect(Collectors.toList());
    location.setFixedLocationIds(fixedLocationIds);
    int locationId = locationDao.insert(location).getId();
    application.setLocationId(locationId);
    pricingService.calculatePrice(application);
    assertEquals(220500, application.getCalculatedPrice().intValue());
  }

  @Test
  public void testBridgeBanderol() {
    Application application = new Application();
    application.setType(ApplicationType.BRIDGE_BANNER);
    ShortTermRental event = new ShortTermRental();
    event.setCommercial(false);
    application.setStartTime(ZonedDateTime.parse("2016-11-07T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-12-10T23:00:00+02:00"));
    application.setEvent(event);
    pricingService.calculatePrice(application);
    // Five weeks non-commercial -> 750 EUR
    assertEquals(75000, application.getCalculatedPrice().intValue());
    event.setCommercial(true);
    application.setStartTime(ZonedDateTime.parse("2016-11-14T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-11-28T05:59:59+02:00"));
    pricingService.calculatePrice(application);
    // Two week commercial -> 1500 EUR
    assertEquals(150000, application.getCalculatedPrice().intValue());
  }

  @Test
  public void testCircus() {
    Application application = new Application();
    application.setType(ApplicationType.CIRCUS);
    application.setStartTime(ZonedDateTime.parse("2016-11-07T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-12-10T05:59:59+02:00"));
    pricingService.calculatePrice(application);
    // Thirty-three days -> 6600 EUR
    assertEquals(660000, application.getCalculatedPrice().intValue());
  }

  @Test
  public void testDogTrainingEvent() {
    Application application = new Application();
    application.setType(ApplicationType.DOG_TRAINING_EVENT);
    application.setStartTime(ZonedDateTime.parse("2016-11-07T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-12-10T05:59:59+02:00"));
    Applicant applicant = new Applicant();
    applicant.setName("Hakija");
    applicant.setType(ApplicantType.ASSOCIATION);
    application.setApplicantId(applicantDao.insert(applicant).getId());
    // association -> 50 EUR /event
    pricingService.calculatePrice(application);
    assertEquals(5000, application.getCalculatedPrice().intValue());

    applicant.setType(ApplicantType.COMPANY);
    application.setApplicantId(applicantDao.insert(applicant).getId());
    // Company -> 100 EUR /event
    pricingService.calculatePrice(application);
    assertEquals(10000, application.getCalculatedPrice().intValue());
  }

  @Test
  public void testDogTrainingField() {
    Application application = new Application();
    application.setType(ApplicationType.DOG_TRAINING_FIELD);
    application.setStartTime(ZonedDateTime.parse("2016-11-07T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2018-12-10T05:59:59+02:00"));
    Applicant applicant = new Applicant();
    applicant.setName("Hakija");
    applicant.setType(ApplicantType.ASSOCIATION);
    application.setApplicantId(applicantDao.insert(applicant).getId());
    // association -> 100 EUR /year
    pricingService.calculatePrice(application);
    assertEquals(30000, application.getCalculatedPrice().intValue());

    applicant.setType(ApplicantType.COMPANY);
    application.setApplicantId(applicantDao.insert(applicant).getId());
    // Company -> 200 EUR /year
    pricingService.calculatePrice(application);
    assertEquals(60000, application.getCalculatedPrice().intValue());
  }

  @Test
  public void testKeskuskatuSales() {
    Application application = new Application();
    application.setType(ApplicationType.KESKUSKATU_SALES);
    application.setStartTime(ZonedDateTime.parse("2016-12-03T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2016-12-22T05:59:59+02:00"));
    Location location = new Location();
    location.setAreaOverride(135.5);
    application.setLocationId(locationDao.insert(location).getId());
    // 19 days, 135.5 sqm -> 14 * 14 * 50 + 5 * 14 * 25 = 11550 EUR
    pricingService.calculatePrice(application);
    assertEquals(1155000, application.getCalculatedPrice().intValue());
  }

  @Test
  public void testPromotionOrSales() {
    Application application = new Application();
    application.setType(ApplicationType.PROMOTION_OR_SALES);
    ShortTermRental event = new ShortTermRental();
    event.setLargeSalesArea(true);
    application.setEvent(event);
    application.setStartTime(ZonedDateTime.parse("2016-12-03T06:00:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2018-12-22T05:59:59+02:00"));
    // Large area, price for three years = 150 EUR * 3 = 450 EUR
    pricingService.calculatePrice(application);
    assertEquals(45000, application.getCalculatedPrice().intValue());
    // Small area is free
    event.setLargeSalesArea(false);
    pricingService.calculatePrice(application);
    assertEquals(0, application.getCalculatedPrice().intValue());
  }

  @Test
  public void testSummerTheatre() {
    Application application = new Application();
    application.setType(ApplicationType.SUMMER_THEATER);
    application.setStartTime(ZonedDateTime.parse("2017-06-15T08:30:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2017-08-10T23:59:59+02:00"));
    // Two months -> 240 EUR
    pricingService.calculatePrice(application);
    assertEquals(24000, application.getCalculatedPrice().intValue());
  }

  @Test
  public void testUrbanFarming() {
    Application application = new Application();
    application.setType(ApplicationType.URBAN_FARMING);
    application.setStartTime(ZonedDateTime.parse("2017-05-15T08:30:00+02:00"));
    application.setEndTime(ZonedDateTime.parse("2019-09-10T23:59:59+02:00"));
    Location location = new Location();
    location.setAreaOverride(222.2);
    application.setLocationId(locationDao.insert(location).getId());
    // Three terms, 222.2 sqm -> 223 * 2 * 3 = 1338 EUR
    pricingService.calculatePrice(application);
    assertEquals(133800, application.getCalculatedPrice().intValue());
  }
}
