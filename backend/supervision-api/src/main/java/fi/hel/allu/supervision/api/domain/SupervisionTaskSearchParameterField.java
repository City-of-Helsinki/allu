package fi.hel.allu.supervision.api.domain;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Supervision task search parameter fields")
public enum SupervisionTaskSearchParameterField implements SearchField {
  APPLICATION_IDENTIFIER("application.applicationId"),
  VALID_AFTER("plannedFinishingTime"),
  VALID_BEFORE("plannedFinishingTime"),
  OWNER_USERNAME("owner.realName"),
  OWNER_ID("owner.realName"),
  TYPE("type"),
  STATUS("status");

  private String sortFieldName;

  private SupervisionTaskSearchParameterField(String sortFieldName) {
    this.sortFieldName = sortFieldName;
  }

  @Override
  public String getSortFieldName() {
    return sortFieldName;
  }
}
