package fi.hel.allu.servicecore.domain;

import java.util.Collections;
import java.util.List;

/**
 * List of search parameters from UI.
 */
public class QueryParametersJson {

  private List<QueryParameterJson> queryParameters = Collections.emptyList();
  private Sort sort;

  public List<QueryParameterJson> getQueryParameters() {
    return queryParameters;
  }

  public void setQueryParameters(List<QueryParameterJson> queryParameters) {
    this.queryParameters = queryParameters;
  }

  public Sort getSort() {
    return sort;
  }

  public void setSort(Sort sort) {
    this.sort = sort;
  }

  public static class Sort {

    public enum Direction {
      ASC,
      DESC
    }

    public Sort() {
      // JSON deserialization
    }

    public Sort(String field, Direction direction) {
      this.field = field;
      this.direction = direction;
    }

    public String field;
    public Direction direction;
  }
}
