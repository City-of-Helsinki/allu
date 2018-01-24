package fi.hel.allu.search.service;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Class for storing index management information (primary and temp index names,
 * syncing status, etc).
 */
public class IndexConductor {
  private final String indexAliasName;
  private String currentIndexName;
  private String newIndexName;
  private final AtomicReference<SyncState> syncState;

  private enum SyncState {
    NOT_ACTIVE, ACTIVE, ACTIVATING, DEACTIVATING
  };

  public IndexConductor(String indexAliasName) {
    this.indexAliasName = indexAliasName;
    syncState = new AtomicReference<>(SyncState.NOT_ACTIVE);
  }

  public String getIndexAliasName() {
    return indexAliasName;
  }

  public String getCurrentIndexName() {
    return currentIndexName;
  }

  public void setCurrentIndexName(String indexName) {
    currentIndexName = indexName;
  }

  public String getNewIndexName() {
    return newIndexName;
  }

  public String generateNewIndexName() {
    do {
      newIndexName = indexAliasName + "_" + RandomStringUtils.randomAlphabetic(10).toLowerCase();
    }
    while (newIndexName.equals(currentIndexName));
    return newIndexName;
  }

  public void commitNewIndex() {
    currentIndexName = newIndexName;
    newIndexName = null;
  }

  public boolean isSyncActive() {
    return !SyncState.NOT_ACTIVE.equals(syncState.get());
  }

  public boolean tryStartSync() {
    return syncState.compareAndSet(SyncState.NOT_ACTIVE, SyncState.ACTIVATING);
  }

  public boolean tryDeactivateSync() {
    return syncState.compareAndSet(SyncState.ACTIVE, SyncState.DEACTIVATING);
  }

  public void setSyncPassive() {
    syncState.set(SyncState.NOT_ACTIVE);
  }

  public void setSyncActive() {
    syncState.set(SyncState.ACTIVE);
  }
}
