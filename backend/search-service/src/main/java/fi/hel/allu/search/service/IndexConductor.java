package fi.hel.allu.search.service;

/**
 * Class for storing index management information (primary and temp index names,
 * syncing status, etc).
 */
public class IndexConductor {
  private final String indexName;
  private final String tempIndexName;
  private boolean isSyncActive;

  public IndexConductor(String indexName, String tempIndexName) {
    this.indexName = indexName;
    this.tempIndexName = tempIndexName;
  }

  public String getIndexName() {
    return indexName;
  }

  public String getTempIndexName() {
    return tempIndexName;
  }

  public boolean isSyncActive() {
    return isSyncActive;
  }

  public void setSyncActive(boolean isSyncActive) {
    this.isSyncActive = isSyncActive;
  }

}
