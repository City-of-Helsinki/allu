package fi.hel.allu.model.service;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.OutdoorEventNature;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.FixedLocation;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.OutdoorEvent;

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

}
