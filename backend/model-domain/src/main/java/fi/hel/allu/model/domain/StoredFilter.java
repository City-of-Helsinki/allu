package fi.hel.allu.model.domain;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;
import fi.hel.allu.common.domain.types.StoredFilterType;

/**
 * Represents user specific search filters. Filters include map filters, application and
 * supervision task filters
 */
public class StoredFilter {
  private Integer id;
  @NotNull
  private StoredFilterType type;
  @NotBlank
  private String name;
  private boolean defaultFilter;
  @NotBlank
  private String filter;
  @NotNull
  private Integer userId;

  public StoredFilter() {}

  public StoredFilter(Integer id, StoredFilterType type, String name, boolean defaultFilter, String filter, Integer userId) {
    this.id = id;
    this.type = type;
    this.name = name;
    this.defaultFilter = defaultFilter;
    this.filter = filter;
    this.userId = userId;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public StoredFilterType getType() {
    return type;
  }

  public void setType(StoredFilterType type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isDefaultFilter() {
    return defaultFilter;
  }

  public void setDefaultFilter(boolean defaultFilter) {
    this.defaultFilter = defaultFilter;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }
}
