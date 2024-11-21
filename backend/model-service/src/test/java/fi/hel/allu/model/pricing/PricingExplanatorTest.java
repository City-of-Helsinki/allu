package fi.hel.allu.model.pricing;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationSpecifier;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.util.Printable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingExplanatorTest {

  private static final int PERIODS_START_YEAR = 2020;
  private static final int PERIODS_END_YEAR = 2020;
  private static final int PERIOD_START_MONTH = 6;
  private static final int PERIOD_END_MONTH = 9;
  private static final int TERMINATION_YEAR = 2022;
  private static final ZonedDateTime TERMINATION_DATE = ZonedDateTime.of(TERMINATION_YEAR, 8, 31, 0, 0, 0, 0, TimeUtil.HelsinkiZoneId);
  private static final int APPLICATION_ID = 99;

  @Mock
  private LocationDao locationDao;
  @InjectMocks
  private PricingExplanator pricingExplanator;

  @Test
  public void test_formatExplanation_excavation() {
    Application application = createExcavationApplication();
    Location location = createLocation(application);
    when(locationDao.findByApplicationId(anyInt())).thenReturn(Collections.singletonList(location));
    List<String> explanations = pricingExplanator.getExplanationWithCustomPeriod(application, "periodId");
    assertEquals(1, explanations.size());
    assertEquals(getExpectedLocationExplanation(location),
      explanations.get(0), "Did not contain expected string");
  }

  @Test
  public void test_formatExplanation_summerTerrace() {
    Application application = createSummerTerraceApplication();
    Location location = createLocation(application);
    when(locationDao.findByApplicationId(anyInt())).thenReturn(Collections.singletonList(location));
    List<String> explanations = pricingExplanator.getExplanationWithCustomPeriod(application, "periodId");
    assertEquals(1, explanations.size());
    assertEquals(getExpectedLocationExplanation(location),
      explanations.get(0), "Did not contain expected string");
  }

  @Test
  public void test_formatExplanation_parklet() {
    Application application = createParkletApplication();
    Location location = createLocation(application);
    when(locationDao.findByApplicationId(anyInt())).thenReturn(Collections.singletonList(location));
    List<String> explanations = pricingExplanator.getExplanationWithCustomPeriod(application, "periodId");
    assertEquals(1, explanations.size());
    assertEquals(getExpectedLocationExplanationForParklet(location),
      explanations.get(0), "Did not contain expected string");
  }

  private Application createExcavationApplication() {
    Application application = new Application();
    application.setId(APPLICATION_ID);
    application.setLocations(Collections.singletonList(new Location()));
    application.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    application.setExtension(new ExcavationAnnouncement());
    Map<ApplicationKind, List<ApplicationSpecifier>> kindListMap = new HashMap<>();
    kindListMap.put(ApplicationKind.STREET_AND_GREEN, Collections.emptyList());
    kindListMap.put(ApplicationKind.YARD, Collections.emptyList());
    application.setKindsWithSpecifiers(kindListMap);
    application.setStartTime(TERMINATION_DATE.withYear(PERIODS_START_YEAR).withMonth(PERIOD_START_MONTH).with(lastDayOfMonth()));
    application.setEndTime(TERMINATION_DATE.withYear(PERIODS_END_YEAR).withMonth(PERIOD_END_MONTH).with(lastDayOfMonth()));
    return application;
  }

  private Application createSummerTerraceApplication() {
    Application application = new Application();
    application.setId(APPLICATION_ID);
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    ShortTermRental str = new ShortTermRental();
    str.setBillableSalesArea(true);
    application.setExtension(str);
    application.setKind(ApplicationKind.SUMMER_TERRACE);
    application.setStartTime(TERMINATION_DATE.withYear(PERIODS_START_YEAR).withMonth(PERIOD_START_MONTH).with(lastDayOfMonth()));
    application.setEndTime(TERMINATION_DATE.withYear(PERIODS_END_YEAR).withMonth(PERIOD_END_MONTH).with(lastDayOfMonth()));
    return application;
  }

  private Application createParkletApplication() {
    Application application = new Application();
    application.setId(APPLICATION_ID);
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    ShortTermRental str = new ShortTermRental();
    str.setBillableSalesArea(true);
    application.setExtension(str);
    application.setKind(ApplicationKind.PARKLET);
    application.setStartTime(TERMINATION_DATE.withYear(PERIODS_START_YEAR).withMonth(PERIOD_START_MONTH).with(lastDayOfMonth()));
    application.setEndTime(TERMINATION_DATE.withYear(PERIODS_END_YEAR).withMonth(PERIOD_END_MONTH).with(lastDayOfMonth()));
    return application;
  }

  private Location createLocation(Application application) {
    Location location = new Location();
    location.setArea(new Double("14.0"));
    location.setPaymentTariff("3");
    location.setPostalAddress(new PostalAddress("Testikatu 42", null, null));
    location.setStartTime(application.getStartTime());
    location.setEndTime(application.getEndTime());
    location.setFixedLocationIds(Collections.emptyList());
    return location;
  }

  private String getExpectedLocationExplanation(Location location) {
    return Printable.forPostalAddress(location.getPostalAddress()) +
      " (periodId), " +
      location.getEffectiveArea().intValue() + "m²";
  }

  private String getExpectedLocationExplanationForParklet(Location location) {
    return Printable.forPostalAddress(location.getPostalAddress()) +
      ", maksuvyöhyke " + location.getEffectivePaymentTariff() + " (periodId), " +
      location.getEffectiveArea().intValue() + "m²";
  }
}