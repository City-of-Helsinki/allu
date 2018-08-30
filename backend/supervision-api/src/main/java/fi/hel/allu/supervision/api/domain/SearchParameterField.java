package fi.hel.allu.supervision.api.domain;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Search parameter fields")
public enum SearchParameterField {

  APPLICATION_IDENTIFIER("applicationId", SearchParameterType.STRING, false),
  NAME("name", SearchParameterType.STRING, false),
  TYPE("type.value", SearchParameterType.STRING, true),
  STATUS("status.value", SearchParameterType.STRING, true),
  DISTRICTS("locations.cityDistrictId", SearchParameterType.STRING, true),
  OWNER("owner.userName", SearchParameterType.STRING, true),
  ADDRESS("address", SearchParameterType.STRING, false),
  APPLICANT_NAME("customers.applicant.customer.name", SearchParameterType.STRING, false),
  CONTACT_NAME("customers.applicant.contacts.name", SearchParameterType.STRING, false),
  VALID_AFTER("recurringApplication", SearchParameterType.DATE, false),
  VALID_BEFORE("recurringApplication", SearchParameterType.DATE, false),
  TAGS("applicationTags", SearchParameterType.STRING, true);

  private final String searchFieldName;
  private final SearchParameterType type;
  private final boolean multiValue;

  private SearchParameterField(String searchFieldName, SearchParameterType type, boolean multiValue) {
    this.searchFieldName = searchFieldName;
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

}
