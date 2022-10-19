package fi.hel.allu.supervision.api.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

public class SearchParameters <T extends SearchField> {

  @NotNull(message = "{search.parameters.required}")
  private Map<T, String> searchParameters;

  @Valid
  private List<SortOrder<T>> sort = new ArrayList<>();

  @NotNull(message = "{search.page.missing}")
  @Min(value = 0, message = "{search.page.invalid}")
  private Integer page;
  @Min(value = 1, message = "{search.pagesize.invalid}")
  private Integer pageSize = Integer.valueOf(100);

  @Schema(description = "Search parameters in key-value map", required = true)
  public Map<T, String> getSearchParameters() {
    return searchParameters;
  }

  public void setSearchParameters(Map<T, String> searchParameters) {
    this.searchParameters = searchParameters;
  }

  @Schema(description = "Zero-based page index.", required = true)
  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  @Schema(description = "Page size (default = 100)")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  @Schema(description = "Search sorting parameters")
  public List<SortOrder<T>> getSort() {
    return sort;
  }

  public void setSort(List<SortOrder<T>> sort) {
    this.sort = sort;
  }


}
