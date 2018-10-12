package fi.hel.allu.model.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.hel.allu.model.domain.ChargeBasisEntry;

public class ChargeBasisModification {

  private List<ChargeBasisEntry> entriesToInsert;
  private Set<Integer> entryIdsToDelete;
  private Map<Integer, ChargeBasisEntry> entriesToUpdate;
  private boolean manuallySet;
  private int applicationId;

  public ChargeBasisModification(int applicationId, List<ChargeBasisEntry> entriesToInsert, Set<Integer> entryIdsToDelete,
      Map<Integer, ChargeBasisEntry> entriesToUpdate, boolean manuallySet) {
    super();
    this.applicationId = applicationId;
    this.entriesToInsert = entriesToInsert;
    this.entryIdsToDelete = entryIdsToDelete;
    this.entriesToUpdate = entriesToUpdate;
    this.manuallySet = manuallySet;
  }

  public List<ChargeBasisEntry> getEntriesToInsert() {
    return entriesToInsert;
  }

  public Set<Integer> getEntryIdsToDelete() {
    return entryIdsToDelete;
  }

  public Map<Integer, ChargeBasisEntry> getEntriesToUpdate() {
    return entriesToUpdate;
  }

  public boolean hasChanges() {
    return !entriesToUpdate.isEmpty() || !entriesToInsert.isEmpty() || !entryIdsToDelete.isEmpty();
  }

  public boolean isManuallySet() {
    return manuallySet;
  }

  public int getApplicationId() {
    return applicationId;
  }

  /**
   * Returns IDs of charge basis entries that have been modified or deleted.
   */
  public Set<Integer> getModifiedEntryIds() {
    Set<Integer> ids = new HashSet<>(entriesToUpdate.keySet());
    ids.addAll(entryIdsToDelete);
    return ids;
  }
}
