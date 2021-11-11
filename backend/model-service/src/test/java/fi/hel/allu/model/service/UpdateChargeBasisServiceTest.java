package fi.hel.allu.model.service;

import fi.hel.allu.model.dao.ChargeBasisDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.SupervisionTaskDao;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.service.chargeBasis.UpdateChargeBasisService;
import org.geolatte.geom.GeometryCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UpdateChargeBasisServiceTest {


  private final List<ChargeBasisEntry> testEntries = new ArrayList<>();
  private final List<ChargeBasisEntry> referenceTagEntries = new ArrayList<>();
  private final List<ChargeBasisEntry> oldEntries = new ArrayList<>();

  @Mock
  ChargeBasisDao chargeBasisDao;

  @Mock
  LocationDao locationDao;

  @Mock
  SupervisionTaskDao supervisionTaskDao;


  @InjectMocks
  UpdateChargeBasisService updateChargeBasisService;

  private List<Location> locations;

  @BeforeEach
  void setUp() {
    ChargeBasisEntry parent = initializeChargeBasisData(2, "ADF#23#24", false, 10);
    parent.setText("not important");
    parent.setInvoicable(false);
    testEntries.add(parent);
    ChargeBasisEntry old = initializeChargeBasisData(3, "ADF#23", false, 10);
    old.setText("not important");
    oldEntries.add(old);
    locations = new ArrayList<>();
    Location testLocation = initalizieLocation();
    testLocation.setId(10);
    locations.add(testLocation);
  }

  @Test
  void updateReferenceTags() {
    ChargeBasisEntry reference1 = initializeReferencingData(200, "ADF#904322", true);
    reference1.setText("altakuljettava");
    reference1.setQuantity(-50);
    referenceTagEntries.add(reference1);
    ChargeBasisEntry reference2 = initializeReferencingData(900, "ADF#23", true);
    reference2.setText("altakuljettava");
    reference2.setQuantity(-50);
    referenceTagEntries.add(reference2);
    when(chargeBasisDao.getReferencingTagEntries(anyInt())).thenReturn(referenceTagEntries);
    when(locationDao.findByIds(anyList())).thenReturn(locations);
    Map<Integer, ChargeBasisEntry> result = updateChargeBasisService
      .getUpdatedManuallySetReferencingEntries(2, testEntries, oldEntries);
    assertEquals(1, result.size());
    assertTrue(result.containsKey(900));
  }

  @Test
  void invoicableUpdatedOnLastApproval() {
    oldEntries.get(0).setInvoicable(true);
    oldEntries.get(0).setInvoicingPeriodId(1);
    List<SupervisionTask> dummyList = new ArrayList<>();
    SupervisionTask dummydata = new SupervisionTask();
    dummydata.setActualFinishingTime(ZonedDateTime.now());
    dummyList.add(dummydata);
    oldEntries.add(initializeChargeBasisData(99, "ADF#9043", false, 10));
    when(locationDao.findByIds(anyList())).thenReturn(locations);
    when(supervisionTaskDao.findFinalSupervisions(anyInt())).thenReturn(dummyList);
    updateChargeBasisService.transferInvoicableStatusFromOldToNew(oldEntries, testEntries, 1);
    assertTrue(testEntries.get(0).isInvoicable());
  }

  @Test
  void invoivableIsNotUpdatedOnLastApproval() {
    oldEntries.get(0).setInvoicable(true);
    oldEntries.get(0).setInvoicingPeriodId(1);
    List<SupervisionTask> dummyList = new ArrayList<>();
    SupervisionTask dummydata = new SupervisionTask();
    dummyList.add(dummydata);
    oldEntries.add(initializeChargeBasisData(99, "ADF#9043", false, 10));
    when(supervisionTaskDao.findFinalSupervisions(anyInt())).thenReturn(dummyList);
    updateChargeBasisService.transferInvoicableStatusFromOldToNew(oldEntries, testEntries, 1);
    assertFalse(testEntries.get(0).isInvoicable());
  }


  @Test
  void updateLockedState() {
    oldEntries.get(0).setLocked(true);
    testEntries.get(0).setLocked(false);
    ChargeBasisEntry updateEntry = initializeChargeBasisData(3, "ADF#25", false, 10);
    updateEntry.setLocked(false);
    Map<Integer, ChargeBasisEntry> entriesToUpdate = new HashMap<>();
    entriesToUpdate.put(updateEntry.getId(), updateEntry);
    oldEntries.add(updateEntry);
    updateChargeBasisService.moveOldLockedEntriesToEntriesBeingAdded(oldEntries, testEntries, entriesToUpdate);
    assertTrue(testEntries.stream().anyMatch(ChargeBasisEntry::getLocked));
  }

  @Test
  void updateInvoicable() {
    oldEntries.get(0).setInvoicable(true);
    oldEntries.get(0).setInvoicingPeriodId(1);
    when(locationDao.findByIds(anyList())).thenReturn(locations);
    updateChargeBasisService.transferInvoicableStatusFromOldToNew(oldEntries, testEntries, 1);
    assertTrue(testEntries.get(0).isInvoicable());
  }


  private ChargeBasisEntry initializeChargeBasisData(int id, String tag, Boolean manuallySet, int locationId) {
    ChargeBasisEntry entry = new ChargeBasisEntry();
    entry.setId(id);
    entry.setTag(tag);
    entry.setManuallySet(manuallySet);
    entry.setLocationId(locationId);
    return entry;
  }

  private ChargeBasisEntry initializeReferencingData(int id, String referedTag, Boolean manuallySet) {
    ChargeBasisEntry entry = new ChargeBasisEntry();
    entry.setId(id);
    entry.setReferredTag(referedTag);
    entry.setManuallySet(manuallySet);
    return entry;
  }

  private Location initalizieLocation() {
    Location location = new Location();
    location.setArea(1.0);
    location.setPostalAddress(new PostalAddress());
    location.setCityDistrictId(1);
    location.setStartTime(ZonedDateTime.now());
    location.setEndTime(ZonedDateTime.now());
    location.setPaymentTariff("");
    location.setUnderpass(false);
    location.setGeometry(new GeometryCollection(null));
    return location;
  }

}
