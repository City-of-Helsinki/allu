package fi.hel.allu.model.dao;

import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChargeBasisDaoUnitTest {

  private final List<ChargeBasisEntry> testEntries = new ArrayList<>();
  private final List<ChargeBasisEntry> referenceTagEntries = new ArrayList<>();
  private final List<ChargeBasisEntry>  oldEntries = new ArrayList<>();

  @Mock
  ChargeBasisDao chargeBasisDao;

  @BeforeEach
  void setUp(){
    ChargeBasisEntry parent = new ChargeBasisEntry();
    parent.setId(2);
    parent.setTag("ADF#23#24");
    parent.setManuallySet(false);
    parent.setLocationId(10);
    parent.setText("parent");
    testEntries.add(parent);
    ChargeBasisEntry old = new ChargeBasisEntry();
    old.setId(2);
    old.setTag("ADF#23");
    old.setManuallySet(false);
    old.setLocationId(10);
    old.setText("old");
    oldEntries.add(old);

    ChargeBasisEntry reference1 = new ChargeBasisEntry();
    reference1.setId(200);
    reference1.setReferredTag("ADF#904322");
    reference1.setText("Altakuljettava");
    referenceTagEntries.add(reference1);
  }


  @Test
  void updateReferenceTagsTest() {
    Map<Integer, Location> locations = new HashMap<>();
    locations.put(10, new Location());
    ChargeBasisEntry reference2 = new ChargeBasisEntry();
    reference2.setId(900);
    reference2.setReferredTag("ADF#23");
    reference2.setManuallySet(true);
    reference2.setText("Altakuljettava");
    referenceTagEntries.add(reference2);
    lenient().when(chargeBasisDao.getReferencingTagEntries(anyInt())).thenReturn(referenceTagEntries);
    when(chargeBasisDao.getEntriesLocations(anyList(),anyList())).thenReturn(locations);
    when(chargeBasisDao.getUpdatedManuallySetReferencingEntries(anyInt(), anyList(),anyList())).thenCallRealMethod();
    Map<Integer, ChargeBasisEntry> result = chargeBasisDao.getUpdatedManuallySetReferencingEntries(2, testEntries, oldEntries);
    assertEquals(1, result.size());
    assertTrue(result.containsKey(900));
  }
}
