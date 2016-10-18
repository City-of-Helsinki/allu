package fi.hel.allu.ui.domain;

import java.util.List;

/**
 * List of search parameters from UI.
 */
public class QueryParametersJson {
  private List<QueryParameterJson> queryParameters;

  public List<QueryParameterJson> getQueryParameters() {
    return queryParameters;
  }

  public void setQueryParameters(List<QueryParameterJson> queryParameters) {
    this.queryParameters = queryParameters;
  }
}
