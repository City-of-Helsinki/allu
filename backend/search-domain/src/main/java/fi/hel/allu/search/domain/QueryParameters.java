package fi.hel.allu.search.domain;

import java.util.List;

public class QueryParameters {
  private List<QueryParameter> queryParameters;
  private Sort sort;

  public List<QueryParameter> getQueryParameters() {
    return queryParameters;
  }

  public void setQueryParameters(List<QueryParameter> queryParameters) {
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
