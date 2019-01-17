package fi.hel.allu.supervision.api.domain;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Application search parameter fields")
public enum ApplicationSearchParameterField implements SearchField {

  APPLICATION_IDENTIFIER("applicationId", "applicationId", SearchParameterType.STRING, false),
  NAME("name", "name", SearchParameterType.STRING, false),
  TYPE("type.value", "type", SearchParameterType.STRING, true),
  STATUS("status.value", "status", SearchParameterType.STRING, true),
  DISTRICTS("locations.cityDistrictId", null, SearchParameterType.STRING, true),
  OWNER("owner.userName", "owner.userName", SearchParameterType.STRING, true),
  ADDRESS("locations.address", null, SearchParameterType.STRING, false),
  APPLICANT_NAME("customers.applicant.customer.name", "customers.applicant.customer.name", SearchParameterType.STRING, false),
  CONTACT_NAME("customers.applicant.contacts.name", null, SearchParameterType.STRING, false),
  VALID_AFTER("recurringApplication", "startTime", SearchParameterType.DATE, false),
  VALID_BEFORE("recurringApplication", "startTime", SearchParameterType.DATE, false),
  TAGS("applicationTags", null, SearchParameterType.STRING, true),
  PROJECT_ID("project.id", null, SearchParameterType.STRING, false);

  private final String searchFieldName;
  private final String sortFieldName;
  private final SearchParameterType type;
  private final boolean multiValue;

  private ApplicationSearchParameterField(String searchFieldName, String sortFieldName, SearchParameterType type, boolean multiValue) {
    this.searchFieldName = searchFieldName;
    this.sortFieldName = sortFieldName;
    this.type = type;
    this.multiValue = multiValue;
  }

  public String getSearchFieldName() {
    return searchFieldName;
  }

  public SearchParameterType getType() {
    return type;
  }

  public boolean isMultiValue() {
    return multiValue;
  }

  @Override
  public String getSortFieldName() {
    return sortFieldName;
  }

}
