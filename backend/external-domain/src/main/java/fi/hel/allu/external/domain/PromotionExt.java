package fi.hel.allu.external.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("Promotion event (promootio) input model.")
public class PromotionExt extends EventExt {

  private List<Integer> fixedLocationIds;

  @ApiModelProperty(value = "IDs of the fixed locations. Should be set if geometry of the application is selected from fixed locations.")
  public List<Integer> getFixedLocationIds() {
    return fixedLocationIds;
  }

  public void setFixedLocationIds(List<Integer> fixedLocationIds) {
    this.fixedLocationIds = fixedLocationIds;
  }
}
