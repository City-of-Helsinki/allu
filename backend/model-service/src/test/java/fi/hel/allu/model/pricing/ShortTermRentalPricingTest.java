package fi.hel.allu.model.pricing;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.*;
import fi.hel.allu.model.domain.*;
import org.geolatte.geom.builder.DSL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.*;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static org.geolatte.geom.builder.DSL.*;
import static org.geolatte.geom.builder.DSL.c;
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

  @Mock
  private PricingDao pricingDao;
  @Mock
  private PricingExplanator pricingExplanator;
  @Mock
  private TerminationDao terminationDao;

  private Application application;
  private List<InvoicingPeriod> invoicingPeriods;

  @Before
  public void setUp() {
    createApplication();
    invoicingPeriods = null;
  }

  @Test
  public void shouldNotUpdateTerracePrice() {
    ShortTermRental str = (ShortTermRental) application.getExtension();
    str.setBillableSalesArea(false);
    application.setExtension(str);
    setShortTermRentalPricing();
    shortTermRentalPricing.calculatePrice();
    verify(pricingDao, times(0)).findValue(ApplicationType.SHORT_TERM_RENTAL, PricingKey.forTerraceKind(application.getKind()), null);
  }

  @Test
  public void shouldUpdateNonRecurringTerracePrice() {
    setShortTermRentalPricing();
    shortTermRentalPricing.calculatePrice();
    verify(pricingDao).findValue(ApplicationType.SHORT_TERM_RENTAL, PricingKey.forTerraceKind(application.getKind()), null);
    verify(pricingExplanator).getExplanationWithCustomPeriod(any(), anyString());
    verify(terminationDao).getTerminationInfo(APPLICATION_ID);
  }

  @Test
  public void shouldUpdateRecurringTerracePrice() {
    setApplicationRecurring();
    setShortTermRentalPricing();
    shortTermRentalPricing.calculatePrice();
    verify(pricingDao).findValue(ApplicationType.SHORT_TERM_RENTAL, PricingKey.forTerraceKind(application.getKind()), null);
    verify(pricingExplanator, times(6)).getExplanationWithCustomPeriod(any(), anyString());
  }

  @Test
  public void shouldUpdateNonRecurringTerracePriceOnTerminated() {
    setApplicationTerminated();
    setShortTermRentalPricing();
    shortTermRentalPricing.calculatePrice();
    verify(pricingDao).findValue(ApplicationType.SHORT_TERM_RENTAL, PricingKey.forTerraceKind(application.getKind()), null);
    verify(pricingExplanator).getExplanationWithCustomPeriod(any(), anyString());
    verify(terminationDao).getTerminationInfo(APPLICATION_ID);
  }

  @Test
  public void shouldUpdateRecurringTerracePriceOnTerminated() {
    setApplicationRecurring();
    setApplicationTerminated();
    setShortTermRentalPricing();
    shortTermRentalPricing.calculatePrice();
    verify(pricingDao).findValue(ApplicationType.SHORT_TERM_RENTAL, PricingKey.forTerraceKind(application.getKind()), null);
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

  private void setShortTermRentalPricing() {
    shortTermRentalPricing = new ShortTermRentalPricing(application, pricingExplanator, pricingDao, terminationDao,
      0, true, invoicingPeriods, Collections.emptyList());
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