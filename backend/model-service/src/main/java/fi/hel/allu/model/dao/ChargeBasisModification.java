package fi.hel.allu.model.dao;

import java.util.*;

import fi.hel.allu.model.domain.ChargeBasisEntry;

public class ChargeBasisModification {

  private final List<ChargeBasisEntry> entriesToInsert;
  private final Set<Integer> entryIdsToDelete;
  private final Map<Integer, ChargeBasisEntry> entriesToUpdate;
  private final boolean manuallySet;
  private final int applicationId;

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

  public ChargeBasisModification filtered(List<Integer> ingoredChargeBasisEntryIds) {
    Map<Integer, ChargeBasisEntry> filteredUpdates = new HashMap<>(entriesToUpdate);
    filteredUpdates.keySet().removeAll(ingoredChargeBasisEntryIds);
    Set<Integer> filteredDelete = new HashSet<>(entryIdsToDelete);
    filteredDelete.removeAll(ingoredChargeBasisEntryIds);
    return new ChargeBasisModification(this.applicationId,
                                       entriesToInsert,
                                       filteredDelete,
                                       filteredUpdates,
                                       manuallySet);
  }
}
