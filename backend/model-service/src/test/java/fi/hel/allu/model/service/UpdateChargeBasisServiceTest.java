package fi.hel.allu.model.service;

import fi.hel.allu.model.dao.ChargeBasisDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.service.chargeBasis.UpdateChargeBasisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UpdateChargeBasisServiceTest {



  private final List<ChargeBasisEntry> testEntries = new ArrayList<>();
  private final List<ChargeBasisEntry> referenceTagEntries = new ArrayList<>();
  private final List<ChargeBasisEntry>  oldEntries = new ArrayList<>();

  @Mock
  ChargeBasisDao chargeBasisDao;

  @Mock
  LocationDao locationDao;

  @InjectMocks
  UpdateChargeBasisService updateChargeBasisService;

  private List<Location> locations;

  @BeforeEach
  void setUp(){
    ChargeBasisEntry parent = initializeChargeBasisData(2,"ADF#23#24",false,10);
    parent.setText("not important");
    testEntries.add(parent);
    ChargeBasisEntry old = initializeChargeBasisData(3,"ADF#23",false,10);
    old.setText("not important");
    oldEntries.add(old);
    locations = new ArrayList<>();
    Location entry1 = new Location();
    entry1.setId(10);
    locations.add(entry1);
  }

  @Test
  void updateReferenceTags() {
    ChargeBasisEntry reference1 = initializeReferencingData(200,"ADF#904322",true);
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

  private ChargeBasisEntry initializeChargeBasisData(int id, String tag, Boolean manuallySet, int locationId){
    ChargeBasisEntry entry = new ChargeBasisEntry();
    entry.setId(id);
    entry.setTag(tag);
    entry.setManuallySet(manuallySet);
    entry.setLocationId(locationId);
    return entry;
  }
  private ChargeBasisEntry initializeReferencingData(int id, String referedTag, Boolean manuallySet){
    ChargeBasisEntry entry = new ChargeBasisEntry();
    entry.setId(id);
    entry.setReferredTag(referedTag);
    entry.setManuallySet(manuallySet);
    return entry;
  }
}
