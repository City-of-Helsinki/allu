package fi.hel.allu.ui.domain;

import java.util.List;

/**
 * List of search parameters from UI.
 */
public class QueryParametersJson {

  private List<QueryParameterJson> queryParameters;
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
