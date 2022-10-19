package fi.hel.allu.supervision.api.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Project search parameter fields")
public enum ProjectSearchParameterField implements SearchField {
  IDENTIFIER("identifier", "identifier"),
  NAME("name", null),
  CUSTOMER_REFERENCE("customerReference", null),
  VALID_AFTER("endTime", "endTime"),
  VALID_BEFORE("startTime", "startTime"),
  OWNER_NAME("ownerName", "ownerName"),
  APPLICATION_IDENTIFIER(null, null);

  private String sortFieldName;
  private String searchFieldName;

  private ProjectSearchParameterField(String searchFieldName, String sortFieldName) {
    this.searchFieldName = searchFieldName;
    this.sortFieldName = sortFieldName;
  }

  @Override
  public String getSortFieldName() {
    return sortFieldName;
  }

  @Override
  public String getSearchFieldName() {
    return searchFieldName;
  }

}
