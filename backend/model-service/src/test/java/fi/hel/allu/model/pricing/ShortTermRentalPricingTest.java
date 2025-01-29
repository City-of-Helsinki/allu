package fi.hel.allu.model.pricing;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.dao.TerminationDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.util.Printable;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShortTermRentalPricingTest {

  private static final int PERIODS_START_YEAR = 2020;
  private static final int PERIODS_END_YEAR = 2025;
  private static final int PERIOD_START_MONTH = 5;
  private static final int PERIOD_END_MONTH = 8;
  private static final int TERMINATION_YEAR = 2022;
  private static final ZonedDateTime TERMINATION_DATE = ZonedDateTime.of(TERMINATION_YEAR, 7, 31, 0, 0, 0, 0, TimeUtil.HelsinkiZoneId);
  private static final int APPLICATION_ID = 99;

  private ShortTermRentalPricing shortTermRentalPricing;

  private Location location;

  @Mock
  private PricingDao pricingDao;
  @Mock
  private PricingExplanator pricingExplanator;
  @Mock
  private LocationDao locationDao;
  private PricingExplanator properPricingExplanator;
  @Mock
  private TerminationDao terminationDao;

  private Application application;
  private Application parkletApplication;
  private List<InvoicingPeriod> invoicingPeriods;

  @Before
  public void setUp() {
    properPricingExplanator = new PricingExplanator(locationDao);
    createApplication();
    createParkletApplication();
    createLocation();
    invoicingPeriods = null;
  }

  @Test
  public void shouldFormatParkletTextAndExplanationCorrect() {
    ShortTermRental str = (ShortTermRental) parkletApplication.getExtension();
    str.setBillableSalesArea(false);
    parkletApplication.setExtension(str);
    setShortTermRentalPricingWithLocationAndProperPricingExplanator();
    when(locationDao.findByApplicationId(anyInt())).thenReturn(Collections.singletonList(location));
    shortTermRentalPricing.calculatePrice();
    Assertions.assertEquals(1, shortTermRentalPricing.getChargeBasisEntries().size(), "No chargeBasis objects created");
    String[] actualExplanation = shortTermRentalPricing.getChargeBasisEntries().get(0).getExplanation();
    Assertions.assertEquals("Testikatu 42, maksuvyöhyke 3" +
        " (" + formatPeriod(parkletApplication) + "), " +
        ((int)Math.ceil(location.getEffectiveArea())) + "m²",
      actualExplanation[0]);
  }

  @Test
  public void shouldNotUpdateTerracePrice() {
    ShortTermRental str = (ShortTermRental) application.getExtension();
    str.setBillableSalesArea(false);
    application.setExtension(str);
    setShortTermRentalPricing();
    shortTermRentalPricing.calculatePrice();
    verify(pricingDao, times(0)).findValue(eq(ApplicationType.SHORT_TERM_RENTAL), eq(PricingKey.forTerraceKind(application.getKind())), eq(null), any());
  }

  @Test
  public void shouldUpdateNonRecurringTerracePrice() {
    setShortTermRentalPricing();
    shortTermRentalPricing.calculatePrice();
    verify(pricingDao).findValue(eq(ApplicationType.SHORT_TERM_RENTAL), eq(PricingKey.forTerraceKind(application.getKind())), eq(null), any());
    verify(pricingExplanator).getExplanationWithCustomPeriod(any(), anyString());
    verify(terminationDao).getTerminationInfo(APPLICATION_ID);
  }

  @Test
  public void shouldUpdateRecurringTerracePrice() {
    setApplicationRecurring();
    setShortTermRentalPricing();
    shortTermRentalPricing.calculatePrice();
    verify(pricingDao).findValue(eq(ApplicationType.SHORT_TERM_RENTAL), eq(PricingKey.forTerraceKind(application.getKind())), eq(null), any());
    verify(pricingExplanator, times(6)).getExplanationWithCustomPeriod(any(), anyString());
  }

  @Test
  public void shouldUpdateNonRecurringTerracePriceOnTerminated() {
    setApplicationTerminated();
    setShortTermRentalPricing();
    shortTermRentalPricing.calculatePrice();
    verify(pricingDao).findValue(eq(ApplicationType.SHORT_TERM_RENTAL), eq(PricingKey.forTerraceKind(application.getKind())), eq(null), any());
    verify(pricingExplanator).getExplanationWithCustomPeriod(any(), anyString());
    verify(terminationDao).getTerminationInfo(APPLICATION_ID);
  }

  @Test
  public void shouldUpdateRecurringTerracePriceOnTerminated() {
    setApplicationRecurring();
    setApplicationTerminated();
    setShortTermRentalPricing();
    shortTermRentalPricing.calculatePrice();
    verify(pricingDao).findValue(eq(ApplicationType.SHORT_TERM_RENTAL), eq(PricingKey.forTerraceKind(application.getKind())), eq(null), any());
    verify(pricingExplanator, times(6)).getExplanationWithCustomPeriod(any(), anyString());
  }

  private Application createApplication() {
    application = new Application();
    application.setId(APPLICATION_ID);
    application.setLocations(Collections.singletonList(new Location()));
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
    parkletApplication = new Application();
    parkletApplication.setId(APPLICATION_ID);
    parkletApplication.setType(ApplicationType.SHORT_TERM_RENTAL);
    ShortTermRental str = new ShortTermRental();
    str.setBillableSalesArea(true);
    parkletApplication.setExtension(str);
    parkletApplication.setKind(ApplicationKind.PARKLET);
    parkletApplication.setStartTime(TERMINATION_DATE.withYear(PERIODS_START_YEAR).withMonth(PERIOD_START_MONTH).with(lastDayOfMonth()));
    parkletApplication.setEndTime(TERMINATION_DATE.withYear(PERIODS_END_YEAR).withMonth(PERIOD_END_MONTH).with(lastDayOfMonth()));
    return parkletApplication;
  }

  private void setShortTermRentalPricing() {
    shortTermRentalPricing = new ShortTermRentalPricing(application, pricingExplanator, pricingDao, terminationDao,
      0, true, invoicingPeriods, Collections.emptyList());
  }

  private void setShortTermRentalPricingWithLocationAndProperPricingExplanator() {
    parkletApplication.setLocations(Collections.singletonList(location));
    shortTermRentalPricing = new ShortTermRentalPricing(parkletApplication, properPricingExplanator, pricingDao, terminationDao,
      location.getArea(), true, invoicingPeriods, Collections.singletonList(location));
  }

  private void createLocation() {
    location = new Location();
    location.setArea(new Double("14.0"));
    location.setPaymentTariff("3");
    location.setPostalAddress(new PostalAddress("Testikatu 42", null, null));
    location.setStartTime(application.getStartTime());
    location.setEndTime(application.getEndTime());
    location.setFixedLocationIds(Collections.emptyList());
  }

  private String formatPeriod(Application application) {
    return Printable.forDayPeriod(application.getStartTime(), application.getEndTime());
  }

  private void setApplicationRecurring() {
    application.setRecurringEndTime(application.getEndTime().withYear(PERIODS_END_YEAR));
    createTestPeriods();
  }

  private void createTestPeriods() {
    invoicingPeriods = new ArrayList<>();
    for (int year = PERIODS_START_YEAR; year <= PERIODS_END_YEAR; year++) {
      invoicingPeriods.add(createPeriod(year));
    }
  }

  private InvoicingPeriod createPeriod(int year) {
    InvoicingPeriod period = new InvoicingPeriod(APPLICATION_ID,
      TERMINATION_DATE.withYear(year).withMonth(PERIOD_START_MONTH).withDayOfMonth(1),
      TERMINATION_DATE.withYear(year).withMonth(PERIOD_END_MONTH).with(lastDayOfMonth())
    );
    period.setId(year);
    return period;
  }

  private void setApplicationTerminated() {
    TerminationInfo terminationInfo = new TerminationInfo();
    terminationInfo.setExpirationTime(TERMINATION_DATE);
    when(terminationDao.getTerminationInfo(APPLICATION_ID)).thenReturn(terminationInfo);
  }
}
