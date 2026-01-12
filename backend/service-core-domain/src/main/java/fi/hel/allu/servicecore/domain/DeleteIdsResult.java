package fi.hel.allu.servicecore.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Result of deleting multiple IDs, including which IDs were deleted and which were skipped")
public record DeleteIdsResult(
  @Schema(description = "List of ids which were deleted from the database") List<Integer> deletedIds,
  @Schema(description = "List of skipped ids which were not deleted from the database") List<Integer> skippedIds
) {
  public DeleteIdsResult {
    // Canonical constructor which normalizes nulls to empty lists
    deletedIds = deletedIds == null ? List.of() : List.copyOf(deletedIds);
    skippedIds = skippedIds == null ? List.of() : List.copyOf(skippedIds);
  }
   public boolean isPartial() {
     return !skippedIds.isEmpty();
  }
}
