package fi.hel.allu.supervision.api.domain;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Contact search parameter fields")
public enum ContactSearchParameterField implements SearchField {

  NAME("name", "name", SearchParameterType.STRING);

  private final String searchFieldName;
  private final String sortFieldName;
  private final SearchParameterType type;

  private ContactSearchParameterField(String searchFieldName, String sortFieldName, SearchParameterType type) {
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
