package fi.hel.allu.supervision.api.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;
import io.swagger.annotations.ApiModelProperty;

public class SearchParameters <T extends SearchField> {

  @NotNull(message = "{search.parameters.required}")
  private Map<T, String> searchParameters;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry intersectingGeometry;

  @Valid
  private List<SortOrder<T>> sort = new ArrayList<>();

  @NotNull(message = "{search.page.missing}")
  @Min(value = 0, message = "{search.page.invalid}")
  private Integer page;
  @Min(value = 1, message = "{search.pagesize.invalid}")
  private Integer pageSize = Integer.valueOf(100);

  @ApiModelProperty(value = "Search parameters in key-value map", required = true)
  public Map<T, String> getSearchParameters() {
    return searchParameters;
  }

  public void setSearchParameters(Map<T, String> searchParameters) {
    this.searchParameters = searchParameters;
  }

  @ApiModelProperty(value = "Zero-based page index.", required = true)
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

  @ApiModelProperty(value =
      "Geometry intersecting application geometry in <a href=\"https://tools.ietf.org/html/rfc7946\">GeoJSON</a> with following limitations:"
        + "<ul>"
        + "<li>Feature / FeatureCollection is currently not supported, geometry should be given as <a href=\"https://tools.ietf.org/html/rfc7946#section-3.1.8\">GeometryCollection</a>.</li>"
        + "<li>Only named CRS is supported, the given name must either be of the form: urn:ogc:def:crs:EPSG:x.y:4326 (x.y: the version of the EPSG) or of the form EPSG:4326</li>"
        + "</ul>"
      )
  public Geometry getIntersectingGeometry() {
    return intersectingGeometry;
  }

  public void setIntersectingGeometry(Geometry intersectingGeometry) {
    this.intersectingGeometry = intersectingGeometry;
  }

  @ApiModelProperty(value = "Search sorting parameters")
  public List<SortOrder<T>> getSort() {
    return sort;
  }

  public void setSort(List<SortOrder<T>> sort) {
    this.sort = sort;
  }


}
