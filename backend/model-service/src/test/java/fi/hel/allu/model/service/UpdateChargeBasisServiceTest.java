package fi.hel.allu.model.service;

import fi.hel.allu.model.dao.ChargeBasisDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.service.chargeBasis.UpdateChargeBasisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UpdateChargeBasisServiceTest {



  private final List<ChargeBasisEntry> testEntries = new ArrayList<>();
  private final List<ChargeBasisEntry> referenceTagEntries = new ArrayList<>();
  private final List<ChargeBasisEntry>  oldEntries = new ArrayList<>();

  @Mock
  ChargeBasisDao chargeBasisDao;

  @Mock
  LocationDao locationDao;

  @Mock
  InvoicingPeriodService invoicingPeriodService;

  @InjectMocks
  UpdateChargeBasisService updateChargeBasisService;

  private List<Location> locations;

  @BeforeEach
  void setUp(){
    ChargeBasisEntry parent = initializeChargeBasisData(2,"ADF#23#24",false,10);
    testEntries.add(parent);
    ChargeBasisEntry old = initializeChargeBasisData(3,"ADF#23",false,10);
    oldEntries.add(old);
    locations = new ArrayList<>();
    Location entry1 = new Location();
    entry1.setId(10);
    locations.add(entry1);
  }

  @Test
  void updateReferenceTags() {
    ChargeBasisEntry reference1 = initializeReferencingData(200,"ADF#904322",true);
    referenceTagEntries.add(reference1);
    ChargeBasisEntry reference2 = initializeReferencingData(900, "ADF#23", true);
    referenceTagEntries.add(reference2);
    when(chargeBasisDao.getReferencingTagEntries(anyInt())).thenReturn(referenceTagEntries);
    when(locationDao.findByIds(anyList())).thenReturn(locations);
    Map<Integer, ChargeBasisEntry> result = updateChargeBasisService
      .getUpdatedManuallySetReferencingEntries(2,testEntries,oldEntries);
    assertEquals(1, result.size());
    assertTrue(result.containsKey(900));
  }

  @Test
  void updateInvoicable() {
    oldEntries.get(0).setInvoicable(true);
    when(locationDao.findByIds(anyList())).thenReturn(locations);
    updateChargeBasisService.transferInvoicableStatusFromOldToNew(oldEntries,testEntries);
    assertTrue(testEntries.get(0).isInvoicable());
  }

  @Test
  void updateLockedState() {
    oldEntries.get(0).setLocked(true);
    testEntries.get(0).setLocked(false);
    ChargeBasisEntry updateEntry =  initializeChargeBasisData(3,"ADF#25", false, 10);
    updateEntry.setLocked(false);
    Map<Integer, ChargeBasisEntry> entriesToUpdate = new HashMap<>();
    entriesToUpdate.put(updateEntry.getId(), updateEntry);
    oldEntries.add(updateEntry);
    updateChargeBasisService.moveOldLockedEntriesToEntriesBeingAdded(oldEntries,testEntries,entriesToUpdate);
    assertTrue(testEntries.stream().anyMatch(ChargeBasisEntry::getLocked));
  }

  @Test
  void updateInvoicePeriodId(){
    int applicationId = 42;
    int firstInvoicePeriod = 1;
    int secondInvoicePeriod = 2;
    oldEntries.add(initializeReferencingData(900, "ADF#23", false));
    oldEntries.add(initializeChargeBasisData(4,"ADF#24",false,10));
    oldEntries.add(initializeReferencingData(999, "ADF#24", false));
    List<InvoicingPeriod> invoicePeriods = new ArrayList<>();
    invoicePeriods.add(inializePeriod(firstInvoicePeriod, ZonedDateTime.now()));
    invoicePeriods.add(inializePeriod(secondInvoicePeriod, ZonedDateTime.now().plusMonths(1)));
    when(invoicingPeriodService.findForApplicationId(applicationId)).thenReturn(invoicePeriods);
    Map<Integer, ChargeBasisEntry> result = updateChargeBasisService.updateInvoivePeriod(applicationId, oldEntries);
    assertEquals(4, result.size());
    assertEquals(result.get(3).getInvoicingPeriodId(), firstInvoicePeriod);
    assertEquals(result.get(4).getInvoicingPeriodId(), secondInvoicePeriod);
    assertEquals(result.get(999).getInvoicingPeriodId(), secondInvoicePeriod);
  }

  private ChargeBasisEntry initializeChargeBasisData(int id, String tag, Boolean manuallySet, int locationId){
    ChargeBasisEntry entry = new ChargeBasisEntry();
    entry.setId(id);
    entry.setTag(tag);
    entry.setManuallySet(manuallySet);
    entry.setLocationId(locationId);
    entry.setText("not important");
    return entry;
  }
  private ChargeBasisEntry initializeReferencingData(int id, String referedTag, Boolean manuallySet){
    ChargeBasisEntry entry = new ChargeBasisEntry();
    entry.setId(id);
    entry.setReferredTag(referedTag);
    entry.setManuallySet(manuallySet);
    entry.setText("altakuljettava");
    entry.setQuantity(-50);
    return entry;
  }

  private InvoicingPeriod inializePeriod(int id, ZonedDateTime startime){
    InvoicingPeriod invoicingPeriod = new InvoicingPeriod();
    invoicingPeriod.setId(id);
    invoicingPeriod.setStartTime(startime);
    return invoicingPeriod;
  }
}
