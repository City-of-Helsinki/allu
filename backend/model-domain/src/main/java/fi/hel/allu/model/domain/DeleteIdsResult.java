package fi.hel.allu.model.domain;

import java.util.Collection;
import java.util.List;

public class DeleteIdsResult {
  private final List<Integer> deletedIds;
  private final List<Integer> skippedIds;

  public DeleteIdsResult(Collection<Integer> deletedIds, Collection<Integer> skippedIds) {
    this.deletedIds = List.copyOf(deletedIds);
    this.skippedIds = List.copyOf(skippedIds);
  }

  public List<Integer> getDeletedIds() {
    return deletedIds;
  }

  public List<Integer> getSkippedIds() {
    return skippedIds;
  }

  public boolean isPartial() {
    return !skippedIds.isEmpty();
  }
}
