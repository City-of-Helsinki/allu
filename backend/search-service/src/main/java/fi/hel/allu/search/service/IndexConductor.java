package fi.hel.allu.search.service;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Class for storing index management information (primary and temp index names,
 * syncing status, etc).
 */
public class IndexConductor {
  private static final AtomicReference<SyncState> SYNC_STATE = new AtomicReference<>(SyncState.NOT_ACTIVE);

  private final String indexAliasName;
  private String currentIndexName;
  private String newIndexName;

  private enum SyncState {
    NOT_ACTIVE, ACTIVE, ACTIVATING, DEACTIVATING
  };

  public IndexConductor(String indexAliasName) {
    this.indexAliasName = indexAliasName;
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

  public final boolean isSyncActive() {
    return !SyncState.NOT_ACTIVE.equals(SYNC_STATE.get());
  }

  public final boolean tryStartSync() {
    return SYNC_STATE.compareAndSet(SyncState.NOT_ACTIVE, SyncState.ACTIVATING);
  }

  public final boolean tryDeactivateSync() {
    return SYNC_STATE.compareAndSet(SyncState.ACTIVE, SyncState.DEACTIVATING);
  }

  public final void setSyncPassive() {
    SYNC_STATE.set(SyncState.NOT_ACTIVE);
  }

  public final void setSyncActive() {
    SYNC_STATE.set(SyncState.ACTIVE);
  }
}
