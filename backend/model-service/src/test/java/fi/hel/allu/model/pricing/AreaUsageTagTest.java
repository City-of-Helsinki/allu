package fi.hel.allu.model.pricing;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.types.ChargeBasisType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.ChargeBasisDao;
import fi.hel.allu.model.dao.ChargeBasisModification;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.service.ChargeBasisService;
import fi.hel.allu.model.service.InvoicingPeriodService;
import fi.hel.allu.model.service.PricingService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;

@RunWith(MockitoJUnitRunner.class)
public class AreaUsageTagTest {

  private ChargeBasisService chargeBasisService;

  @Mock
  private ChargeBasisDao chargeBasisDao;
  @Mock
  private ApplicationDao applicationDao;
  @Mock
  private InvoicingPeriodService invoicingPeriodService;
  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Mock
  private PricingService pricingService;
  @Captor
  private ArgumentCaptor<List<ChargeBasisEntry>> captor;

  @Before
  public void setUp() {
    chargeBasisService = new ChargeBasisService(chargeBasisDao, applicationDao, eventPublisher, invoicingPeriodService, pricingService);
    Mockito.when(invoicingPeriodService.findFirstOpenPeriod(anyInt())).thenReturn(Optional.empty());
  }

  @Test
  public void setTagCorrectly() {
    ChargeBasisEntry e1 = new ChargeBasisEntry(null, null, true, ChargeBasisType.AREA_USAGE_FEE,
        ChargeBasisUnit.PIECE, 1, "Entry 1", new String[] { "One entry", "Item" }, 1, 1);
    List<ChargeBasisEntry> entries = Arrays.asList(e1);
    Mockito.when(chargeBasisDao.getModifications(Mockito.eq(1), captor.capture(), Mockito.eq(true)))
        .thenReturn(new ChargeBasisModification(0, entries, Collections.emptySet(), Collections.emptyMap(), false));
    chargeBasisService.setManualChargeBasis(1, entries);
    final List<ChargeBasisEntry> savedEntries = captor.getValue();
    assertEquals(1, savedEntries.size());
    final ChargeBasisEntry entry = savedEntries.get(0);
    assertEquals("ArUs1", entry.getTag());
  }

  @Test
  public void discountForAreaUsage() {
    ChargeBasisEntry e1 = new ChargeBasisEntry("ArUs1", null, true, ChargeBasisType.AREA_USAGE_FEE,
        ChargeBasisUnit.PIECE, 1, "Entry 1", new String[] { "One entry", "Item" }, 1, 1);
    ChargeBasisEntry e2 = new ChargeBasisEntry(null, "ArUs1", true, ChargeBasisType.DISCOUNT,
        ChargeBasisUnit.PIECE, 1, "Entry 2", new String[] { "Discount entry", "Discount" }, 1, 1);

    List<ChargeBasisEntry> entries = Arrays.asList(e1, e2);
    Mockito.when(chargeBasisDao.getModifications(Mockito.eq(1), captor.capture(), Mockito.eq(true)))
        .thenReturn(new ChargeBasisModification(0, entries, Collections.emptySet(), Collections.emptyMap(), false));
    chargeBasisService.setManualChargeBasis(1, entries);
    final List<ChargeBasisEntry> savedEntries = captor.getValue();
    assertEquals(2, savedEntries.size());
    final ChargeBasisEntry entry1 = savedEntries.get(0);
    assertEquals("ArUs1", entry1.getTag());
    final ChargeBasisEntry entry2 = savedEntries.get(1);
    assertEquals(null, entry2.getTag());
    assertEquals("ArUs1", entry2.getReferredTag());
  }

  @Test
  public void addSecondAreaUsageFee() {
    ChargeBasisEntry e1 = new ChargeBasisEntry("ArUs1", null, true, ChargeBasisType.AREA_USAGE_FEE,
        ChargeBasisUnit.PIECE, 1, "Entry 1", new String[] { "One entry", "Item" }, 1, 1);
    ChargeBasisEntry e2 = new ChargeBasisEntry(null, null, true, ChargeBasisType.AREA_USAGE_FEE,
        ChargeBasisUnit.PIECE, 1, "Entry 2", new String[] { "One entry", "Item" }, 1, 1);

    List<ChargeBasisEntry> entries = Arrays.asList(e1, e2);
    Mockito.when(chargeBasisDao.getModifications(Mockito.eq(1), captor.capture(), Mockito.eq(true)))
    .thenReturn(new ChargeBasisModification(0, entries, Collections.emptySet(), Collections.emptyMap(), false));
    chargeBasisService.setManualChargeBasis(1, entries);

    final List<ChargeBasisEntry> savedEntries = captor.getValue();
    assertEquals(2, savedEntries.size());
    final ChargeBasisEntry entry1 = savedEntries.get(0);
    assertEquals("ArUs1", entry1.getTag());
    assertEquals(null, entry1.getReferredTag());
    final ChargeBasisEntry entry2 = savedEntries.get(1);
    assertEquals("ArUs2", entry2.getTag());
    assertEquals(null, entry2.getReferredTag());
  }
}
