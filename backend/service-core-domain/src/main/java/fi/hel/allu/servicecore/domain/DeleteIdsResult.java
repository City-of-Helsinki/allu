package fi.hel.allu.servicecore.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;
import java.util.List;

@Schema(description = "Result of deleting multiple IDs, including which IDs were deleted and which were skipped")
public class DeleteIdsResult {
  @Schema(description = "List of ids which were deleted from the database") private final List<Integer> deletedIds;
  @Schema(description = "List of skipped ids which were not deleted from the database") private final List<Integer> skippedIds;

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
