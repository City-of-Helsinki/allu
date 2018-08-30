package fi.hel.allu.supervision.api.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.geolatte.geom.Geometry;
import org.springframework.data.domain.Sort.Direction;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Application search parameters")
public class SearchParameters {

  private Map<SearchParameterField, String> searchParameters;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry intersectingGeometry;

  private List<SortOrder> sort = new ArrayList<>();

  private Integer page;
  private Integer pageSize = Integer.valueOf(100);

  @ApiModelProperty(value = "Search parameters", required = true)
  public Map<SearchParameterField, String> getSearchParameters() {
    return searchParameters;
  }

  public void setSearchParameters(Map<SearchParameterField, String> searchParameters) {
    this.searchParameters = searchParameters;
  }

  @ApiModelProperty(value = "Page number (default 0)")
  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  @ApiModelProperty(value = "Page size (default = 100)")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  @ApiModelProperty(value = "Geometry intersecting application geometry")
  public Geometry getIntersectingGeometry() {
    return intersectingGeometry;
  }

  public void setIntersectingGeometry(Geometry intersectingGeometry) {
    this.intersectingGeometry = intersectingGeometry;
  }

  @ApiModelProperty(value = "Sort")
  public List<SortOrder> getSort() {
    return sort;
  }

  public void setSort(List<SortOrder> sort) {
    this.sort = sort;
  }

  public static class SortOrder {
    SearchParameterField field;
    Direction direction;
    public SearchParameterField getField() {
      return field;
    }
    public void setField(SearchParameterField field) {
      this.field = field;
    }
    public Direction getDirection() {
      return direction;
    }
    public void setDirection(Direction direction) {
      this.direction = direction;
    }
  }

}
