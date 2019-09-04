package fi.hel.allu.supervision.api.domain;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Customer search parameter fields")
public enum CustomerSearchParameterField implements SearchField {

  NAME("name", "name", SearchParameterType.STRING),
  REGISTRY_KEY("registryKey", "registryKey", SearchParameterType.STRING);

  private final String searchFieldName;
  private final String sortFieldName;
  private final SearchParameterType type;

  private CustomerSearchParameterField(String searchFieldName, String sortFieldName, SearchParameterType type) {
    this.searchFieldName = searchFieldName;
    this.sortFieldName = sortFieldName;
    this.type = type;
  }

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
