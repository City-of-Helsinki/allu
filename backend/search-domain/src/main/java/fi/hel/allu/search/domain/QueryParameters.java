package fi.hel.allu.search.domain;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QueryParameters {

  private Map<String, QueryParameter> queryParameters = Collections.emptyMap();
  private boolean matchAny;

  public List<QueryParameter> getQueryParameters() {
    return new ArrayList<>(queryParameters.values());
  }

  public void setQueryParameters(List<QueryParameter> queryParameters) {
    this.queryParameters = queryParameters.stream()
        .collect(Collectors.toMap(qp -> qp.getFieldName(), Function.identity()));
  }

  public boolean isMatchAny() {
    return matchAny;
  }

  public void setMatchAny(boolean matchAny) {
    this.matchAny = matchAny;
  }

  public QueryParameter getParameter(String fieldName) {
    return queryParameters.get(fieldName);
  }

  public QueryParameter remove(String fieldName) {
    return queryParameters.remove(fieldName);
  }
}
