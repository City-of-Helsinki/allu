package fi.hel.allu.servicecore.domain;

import java.util.Collections;
import java.util.List;

/**
 * List of search parameters from UI.
 */
public class QueryParametersJson {
  private boolean matchAny;
  private List<QueryParameterJson> queryParameters = Collections.emptyList();

  public List<QueryParameterJson> getQueryParameters() {
    return queryParameters;
  }

  public void setQueryParameters(List<QueryParameterJson> queryParameters) {
    this.queryParameters = queryParameters;
  }

  public boolean isMatchAny() {
    return matchAny;
  }

  public void setMatchAny(boolean matchAny) {
    this.matchAny = matchAny;
  }
}
