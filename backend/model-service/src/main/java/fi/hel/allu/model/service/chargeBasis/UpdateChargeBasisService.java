package fi.hel.allu.model.service.chargeBasis;


import fi.hel.allu.model.dao.ChargeBasisDao;
import fi.hel.allu.model.dao.ChargeBasisModification;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.service.InvoicingPeriodService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Only used to move data from old chargebasis model to new.
 */
@Service
public class UpdateChargeBasisService {

  private final ChargeBasisDao chargeBasisDao;
  private final LocationDao locationDao;
  private final InvoicingPeriodService invoicingPeriodService;

  public UpdateChargeBasisService(ChargeBasisDao chargeBasisDao, LocationDao locationDao,
                                  InvoicingPeriodService invoicingPeriodService) {
    this.chargeBasisDao = chargeBasisDao;
    this.locationDao = locationDao;
    this.invoicingPeriodService = invoicingPeriodService;
  }

  /**
   * Gives object that have all the modificaton that is needed to make in the charge_basis table in database
   *
   * @param applicationId int
   * @param entries       List<ChargeBasesEntry>
   * @param manuallySet   boolean
   * @return ChargeBasisModification
   */
  public ChargeBasisModification getModifications(int applicationId, List<ChargeBasisEntry> entries, boolean manuallySet) {
    List<ChargeBasisEntry> oldEntries = chargeBasisDao.getChargeBasis(applicationId).stream()
      .filter(e -> e.getManuallySet() == manuallySet).collect(Collectors.toList());
    Map<Integer, ChargeBasisEntry> entriesToUpdate = getEntriesToUpdate(entries, oldEntries);
    List<ChargeBasisEntry> entriesToAdd = getEntriesToAdd(entries,oldEntries);
    transferInvoicableStatusFromOldToNew(oldEntries, entriesToAdd);
    Set<Integer> entryIdsToDelete = oldEntries.stream().filter(oe -> !hasEntryWithKey(entries, oe) && isNotLocked(oe)).map(ChargeBasisEntry::getId).collect(Collectors.toSet());
    moveOldLockedEntriesToEntriesBeingAdded(oldEntries, entriesToAdd, entriesToUpdate);
    entriesToUpdate.putAll(getUpdatedManuallySetReferencingEntries(applicationId, entriesToAdd, oldEntries));
    return new ChargeBasisModification(applicationId, entriesToAdd, entryIdsToDelete, entriesToUpdate, manuallySet);
  }

  private boolean isNotLocked(ChargeBasisEntry entry){
    if (entry.getLocked() == null){
      return true;
    }
    else {
      return !entry.getLocked();
    }
  }

  public List<ChargeBasisEntry> getEntriesToAdd(List<ChargeBasisEntry> entries, List<ChargeBasisEntry> oldEntries){
    return entries.stream().filter(e -> !hasEntryWithKey(oldEntries, e)).collect(Collectors.toList());
  }

  /**
   * Returns map containing entries to update - existing entry ID as key and new entry as value
   */
  private Map<Integer, ChargeBasisEntry> getEntriesToUpdate(List<ChargeBasisEntry> entries,
                                                            List<ChargeBasisEntry> oldEntries) {
    Map<Integer, ChargeBasisEntry> result = new HashMap<>();
    for (ChargeBasisEntry e : entries) {
      ChargeBasisEntry existing = getExistingEntry(e, oldEntries);
      if (existing != null && hasChanges(e, existing) ) {
        result.put(existing.getId(), e);
      }
    }
    return result;
  }


  private ChargeBasisEntry getExistingEntry(ChargeBasisEntry entry, List<ChargeBasisEntry> oldEntries) {
    return oldEntries.stream()
      .filter(oe -> hasSameKey(oe, entry))
      .findFirst()
      .orElse(null);
  }

  private boolean hasChanges(ChargeBasisEntry entry, ChargeBasisEntry old) {
    return !entry.equals(old);
  }

  public boolean hasEntryWithKey(List<ChargeBasisEntry> entries, ChargeBasisEntry entry) {
    return entries.stream().anyMatch(e -> hasSameKey(e, entry));
  }

  private boolean hasSameKey(ChargeBasisEntry entry1, ChargeBasisEntry entry2) {
    return entry1.getManuallySet() ? com.google.common.base.Objects.equal(
      entry1.getId(), entry2.getId())
      : com.google.common.base.Objects.equal(entry1.getTag(), entry2.getTag());
  }


  public Map<Integer, ChargeBasisEntry> getUpdatedManuallySetReferencingEntries(int applicationId, List<ChargeBasisEntry> entriesToAdd, List<ChargeBasisEntry> oldEntries) {
    Map<Integer, ChargeBasisEntry> addedEntries = new HashMap<>();

    if(entriesToAdd.isEmpty()) return addedEntries;

    List<ChargeBasisEntry> underpasses = chargeBasisDao.getReferencingTagEntries(applicationId);

    for (ChargeBasisEntry oldEntry : oldEntries) {
      List<ChargeBasisEntry> referredEntries = underpasses.stream()
        .filter(e -> StringUtils.equals(oldEntry.getTag(), e.getReferredTag()) && e.getManuallySet())
        .collect(Collectors.toList());
      if (!referredEntries.isEmpty()) {

        Map<Integer, Location> locationMap = getEntriesLocations(entriesToAdd, oldEntries);

        ChargeBasisEntry newParentEntry = findSameNewEntry(entriesToAdd, oldEntry, locationMap);
        for (ChargeBasisEntry referredTagEntry : referredEntries) {
          ChargeBasisEntry updatedEntry = updateReferencingTagEntry(newParentEntry, referredTagEntry);
          addedEntries.put(updatedEntry.getId(), updatedEntry);
        }
      }
    }
    return addedEntries;
  }

  private Map<Integer, Location> getEntriesLocations(List<ChargeBasisEntry> entriesToAdd, List<ChargeBasisEntry> oldEntries) {
    Set<Integer> locationIds = new HashSet<>();
    oldEntries.stream().filter(e -> e.getLocationId() != null).forEach(e -> locationIds.add(e.getLocationId()));
    entriesToAdd.stream().filter(e -> e.getLocationId() != null).forEach(e -> locationIds.add(e.getLocationId()));
    return locationDao.findByIds(new ArrayList<>(locationIds))
      .stream().collect(Collectors.toMap(Location::getId, Function.identity()));
  }

  private ChargeBasisEntry findSameNewEntry(List<ChargeBasisEntry> entriesToAdd,
                                            ChargeBasisEntry oldEntry, Map<Integer, Location> locationMap) {
    for (ChargeBasisEntry newEntry : entriesToAdd) {
      if (newEntry.equalContent(oldEntry, locationMap)) return newEntry;
    }
    return new ChargeBasisEntry();
  }

  private ChargeBasisEntry updateReferencingTagEntry(ChargeBasisEntry newParentEntry, ChargeBasisEntry referredTagEntry) {
    referredTagEntry.setReferredTag(newParentEntry.getTag());
    if (newParentEntry.getInvoicingPeriodId() != null) {
      referredTagEntry.setInvoicingPeriodId(newParentEntry.getInvoicingPeriodId());
    } else {
      referredTagEntry.setInvoicingPeriodId(null);
    }
    return referredTagEntry;
  }

  /**
   * Transfers the {@code invoicable} field data from old {@code ChargeBasisEntry} to the new.
   * Check of equality is done by comparing content of each entry. Only calculated entries are updated
   * Without this function, {@code invoicable} field data would be lost on {@code InvoicingPeriod} update.
   *
   * @param oldEntries   {@code ChargeBasisEntry} list before update
   * @param entriesToAdd {@code ChargeBasisEntry} list to add
   */
  public void transferInvoicableStatusFromOldToNew(List<ChargeBasisEntry> oldEntries, List<ChargeBasisEntry> entriesToAdd) {
    // Get locations to compare entire entry
    List<ChargeBasisEntry> oldParentEntries = getParentEntries(oldEntries);
    List<ChargeBasisEntry> newParentEntries = getParentEntries(entriesToAdd);
    List<ChargeBasisEntry> newChildrenEntries = entriesToAdd.stream().filter(e -> e.getReferredTag() != null)
      .collect(Collectors.toList());
    Map<Integer, Location> locationMap = getEntriesLocations(entriesToAdd, oldEntries);
    List<Integer> updatedEntries = new ArrayList<>();
    for (ChargeBasisEntry adding : newParentEntries) {
      Optional<ChargeBasisEntry> oldOptional = oldParentEntries.stream()
        .filter(old -> adding.equalContent(old, locationMap) && !updatedEntries.contains(old.getId()) && equalObject(old, adding))
        .findAny();
      oldOptional.ifPresent(old -> adding.setInvoicable(old.isInvoicable()));
      oldOptional.ifPresent(old -> adding.setLocked(old.getLocked()));
      oldOptional.ifPresent(old -> updatedEntries.add(old.getId()));
      if (oldOptional.isPresent()) {
        List<ChargeBasisEntry> parentChildren = newChildrenEntries.stream()
          .filter(e -> StringUtils.equals(adding.getTag(), e.getReferredTag()))
          .collect(Collectors.toList());
        for (ChargeBasisEntry child : parentChildren) {
          child.setInvoicable(adding.isInvoicable());
          child.setLocked(adding.getLocked());
        }
      }
    }
  }

  private boolean equalObject(ChargeBasisEntry old, ChargeBasisEntry adding){
    if(old.getManuallySet()){
      return hasSameKey(old, adding);
    }
    return true;
  }

  private List<ChargeBasisEntry> getParentEntries(List<ChargeBasisEntry> entries) {
    return entries.stream().filter(e -> e.getReferredTag() == null).collect(Collectors.toList());
  }

  /**
   * Moves entries from {@code entriesToUpdate} map to {@code entriesToAdd}
   * if {@code entriesToUpdate} contains a locked entry.
   * This method is necessary, as entries without a location do not have
   * invoicingPeriodId or locationId in their tag String (see {@link #getEntriesToUpdate}
   * and {@link #hasEntryWithKey}).
   *
   * @param oldEntries      {@code ChargeBasisEntry} list before update
   * @param entriesToAdd    {@code ChargeBasisEntry} list to add
   * @param entriesToUpdate map of {@code ChargeBasisEntry} objects to be updated
   */
  public void moveOldLockedEntriesToEntriesBeingAdded(List<ChargeBasisEntry> oldEntries,
                                                      List<ChargeBasisEntry> entriesToAdd,
                                                      Map<Integer, ChargeBasisEntry> entriesToUpdate) {
    List<ChargeBasisEntry> oldEntriesToBeUpdated = oldEntries.stream()
      .filter(oe -> Boolean.TRUE.equals(oe.getLocked()) && entriesToUpdate.containsKey(oe.getId()))
      .collect(Collectors.toList());
    List<ChargeBasisEntry> entriesToPrependToAddList = new ArrayList<>();
    for (ChargeBasisEntry lockedOldEntryToBeUpdated : oldEntriesToBeUpdated) {
      if (isMoveNecessary(lockedOldEntryToBeUpdated)) {
        ChargeBasisEntry entryToUpdate = entriesToUpdate.get(lockedOldEntryToBeUpdated.getId());
        entryToUpdate.setInvoicable(lockedOldEntryToBeUpdated.isInvoicable());
        entryToUpdate.setLocked(lockedOldEntryToBeUpdated.getLocked());
        // If locationId is null, we can put it first in entryNumber order.
        // Thus, we can sort the entriesToAdd list, before prepending those without locationId.
        if (lockedOldEntryToBeUpdated.getLocationId() == null) {
          entriesToPrependToAddList.add(entryToUpdate);
        } else {
          entriesToAdd.add(entryToUpdate);
        }
        // Remove entry from entriesToUpdate
        entriesToUpdate.remove(lockedOldEntryToBeUpdated.getId());
      }
    }
    // Add entries to entriesToAdd in the following manner,
    // to ensure the locked entries are first in entryNumber order.
    entriesToAdd.addAll(0, entriesToPrependToAddList);
  }

  private boolean isMoveNecessary(ChargeBasisEntry chargeBasisEntry) {
    if (chargeBasisEntry.getInvoicingPeriodId() != null) {
      return !invoicingPeriodService.isLockedPeriod(chargeBasisEntry.getInvoicingPeriodId());
    }
    return true;
  }

}