package fi.hel.allu.supervision.api.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Customer search parameter fields")
public enum CustomerSearchParameterField implements SearchField {

  NAME("name", "name", SearchParameterType.STRING),
  REGISTRY_KEY("registryKey", "registryKey", SearchParameterType.STRING),
  IS_ACTIVE("active", "active", SearchParameterType.BOOLEAN);

  private final String searchFieldName;
  private final String sortFieldName;
  private final SearchParameterType type;

  private CustomerSearchParameterField(String searchFieldName, String sortFieldName, SearchParameterType type) {
    this.searchFieldName = searchFieldName;
    this.sortFieldName = sortFieldName;
    this.type = type;
  }

  @Override
  public String getSearchFieldName() {
    return searchFieldName;
  }

  public SearchParameterType getType() {
    return type;
  }

  @Override
  public String getSortFieldName() {
    return sortFieldName;
  }

}
