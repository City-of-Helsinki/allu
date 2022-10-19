package fi.hel.allu.external.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description ="Promotion event (promootio) input model.")
public class PromotionExt extends EventExt {

  private List<Integer> fixedLocationIds;

  @Schema(description = "IDs of the fixed locations. Should be set if geometry of the application is selected from fixed locations.")
  public List<Integer> getFixedLocationIds() {
    return fixedLocationIds;
  }

  public void setFixedLocationIds(List<Integer> fixedLocationIds) {
    this.fixedLocationIds = fixedLocationIds;
  }
}
