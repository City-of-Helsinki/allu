package fi.hel.allu.servicecore.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import fi.hel.allu.common.domain.types.StoredFilterType;

public class StoredFilterJson {
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
