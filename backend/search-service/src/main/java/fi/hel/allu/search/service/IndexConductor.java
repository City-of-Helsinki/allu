package fi.hel.allu.search.service;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Class for storing index management information (primary and temp index names,
 * syncing status, etc).
 */
public class IndexConductor {
  private final String indexName;
  private final String tempIndexName;
  private AtomicReference<SyncState> syncState;

  private enum SyncState {
    NOT_ACTIVE, ACTIVE, DEACTIVATING
  };

  public IndexConductor(String indexName, String tempIndexName) {
    this.indexName = indexName;
    this.tempIndexName = tempIndexName;
    syncState = new AtomicReference<>(SyncState.NOT_ACTIVE);
  }

  public String getIndexName() {
    return indexName;
  }

  public String getTempIndexName() {
    return tempIndexName;
  }

  public boolean isSyncActive() {
    return !SyncState.NOT_ACTIVE.equals(syncState.get());
  }

  public boolean tryStartSync() {
    return syncState.compareAndSet(SyncState.NOT_ACTIVE, SyncState.ACTIVE);
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
