package fi.hel.allu.supervision.api.mapper;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import fi.hel.allu.supervision.api.domain.SearchParameters;

public class MapperUtil {

  public static PageRequest DEFAULT_PAGE_REQUEST = new PageRequest(0, 100000);

  public static ZonedDateTime parseDate(String value) {
    return ZonedDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
  }

  public static List<String> split(String str){
    return Stream.of(str.split(","))
      .map(elem -> elem.trim())
      .collect(Collectors.toList());
  }

  public static Pageable mapToPageRequest(SearchParameters<?> searchParameters) {
    Sort sort = getSort(searchParameters);
    PageRequest pageRequest = new PageRequest(searchParameters.getPage(), searchParameters.getPageSize(), sort);
    return pageRequest;
  }

  private static Sort getSort(SearchParameters<?> searchParameters) {
    List<Sort.Order> sortOrder = searchParameters.getSort().stream()
      .filter(s -> s.getField().getSortFieldName() != null)
      .map(s -> new Sort.Order(s.getDirection(), s.getField().getSortFieldName()))
      .collect(Collectors.toList());
    return !sortOrder.isEmpty() ? new Sort(sortOrder) : null;
  }

}
