package fi.hel.allu.search.domain;

import java.util.Collections;
import java.util.List;

public class QueryParameters {

  private List<QueryParameter> queryParameters = Collections.emptyList();
  private boolean matchAny;

  public List<QueryParameter> getQueryParameters() {
    return queryParameters;
  }

  public void setQueryParameters(List<QueryParameter> queryParameters) {
    this.queryParameters = queryParameters;
  }

  public boolean isMatchAny() {
    return matchAny;
  }

  public void setMatchAny(boolean matchAny) {
    this.matchAny = matchAny;
  }

}
