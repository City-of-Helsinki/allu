package fi.hel.allu.supervision.api.controller;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.supervision.api.domain.SearchParameterField;
import fi.hel.allu.supervision.api.domain.SearchParameterType;
import fi.hel.allu.supervision.api.domain.SearchParameters;

public class SearchParameterMapper {

  public static Pageable mapToPageRequest(SearchParameters searchParameters) {
    Sort sort = new Sort(searchParameters.getSort().stream()
        .map(s -> new Sort.Order(s.getDirection(), s.getField().getSearchFieldName()))
        .collect(Collectors.toList()));
    PageRequest pageRequest = new PageRequest(searchParameters.getPage(), searchParameters.getPageSize(), sort);
    return pageRequest;
  }

  public static ApplicationQueryParameters mapToQueryParameters(SearchParameters searchParameters) {
    ApplicationQueryParameters result = new ApplicationQueryParameters();
    List<QueryParameter> queryParams = new ArrayList<>();
    for (Entry<SearchParameterField, String> parameter: searchParameters.getSearchParameters().entrySet()) {
      QueryParameter qp = mapQueryParameter(parameter.getKey(), parameter.getValue());
      queryParams.add(qp);
    }
    result.setQueryParameters(queryParams);
    result.setIntersectingGeometry(searchParameters.getIntersectingGeometry());
    return result;
  }

  private static QueryParameter mapQueryParameter(SearchParameterField key, String value) {
    if (key.getType() == SearchParameterType.DATE) {
      return mapDateParameter(key, value);
    } else if (key.isMultiValue()) {
      return new QueryParameter(key.getSearchFieldName(), split(value));
    } else {
      return new QueryParameter(key.getSearchFieldName(), value);
    }
  }

  private static List<String> split(String str){
    return Stream.of(str.split(","))
      .map(elem -> elem.trim())
      .collect(Collectors.toList());
  }

  private static QueryParameter mapDateParameter(SearchParameterField key, String value) {
    if (key == SearchParameterField.VALID_AFTER) {
      return new QueryParameter(key.getSearchFieldName(), parseDate(value), null);
    } else {
      return new QueryParameter(key.getSearchFieldName(), null, parseDate(value));
    }
  }

  private static ZonedDateTime parseDate(String value) {
    return ZonedDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }

}
