package fi.hel.allu.common.wfs;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class WfsFilter {
  public static final String GEOCODE_FILTER = "cql_filter";
  private static final String AND = "AND";
  private static final String OR = "OR";

  private String filter;

  private WfsFilter(WfsFilter current, WfsFilter joinedFilter, String operation) {
    this.filter = String.join(" ", current.filter, operation, joinedFilter.filter);
  }

  public WfsFilter(String property, Object value) {
    filter = createFilter(property, value);
    if (StringUtils.isEmpty(filter)) {
      throw new IllegalArgumentException("Property and value cannot be empty");
    }
  }

  public WfsFilter and(String property, Optional<? extends Object> value) {
    return value
        .map(val -> new WfsFilter(property, val))
        .map(joinedFilter -> new WfsFilter(this, joinedFilter, AND))
        .orElse(this);
  }

  public WfsFilter or(String property, Optional<? extends Object> value) {
    return value
        .map(val -> new WfsFilter(property, val))
        .map(joinedFilter -> new WfsFilter(this, joinedFilter, OR))
        .orElse(this);
  }

  public String build() {
    return String.format("(%s)", filter);
  }

  private String createFilter(String property, Object value) {
    if (StringUtils.isNotEmpty(property) && value != null) {
      String filterTemplate = property + "='%s'";
      return String.format(filterTemplate, value);
    } else {
      return "";
    }
  }
}
