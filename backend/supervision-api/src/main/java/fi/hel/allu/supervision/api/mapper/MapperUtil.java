package fi.hel.allu.supervision.api.mapper;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geolatte.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;
import fi.hel.allu.supervision.api.domain.SearchParameters;

public class MapperUtil {

  public static PageRequest DEFAULT_PAGE_REQUEST = new PageRequest(0, 100000);

  private static final Logger logger = LoggerFactory.getLogger(MapperUtil.class);
  private static ObjectReader geometryReader;

  static {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      SimpleModule module =
        new SimpleModule("CustomGeometrySerializer", new Version(1, 0, 0, null, null, null));
      module.addSerializer(Geometry.class, new GeometrySerializerProxy());
      module.addDeserializer(Geometry.class, new GeometryDeserializerProxy());
      objectMapper.registerModule(module);
      geometryReader = objectMapper.readerFor(Geometry.class);
  }

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

  public static Geometry toGeometry(String geometryJsonString) {
    try {
      return geometryReader.readValue(geometryJsonString);
    } catch (IOException e) {
      logger.warn("Failed to read geometry JSON", e);
      return null;
    }
  }

  private static Sort getSort(SearchParameters<?> searchParameters) {
    List<Sort.Order> sortOrder = searchParameters.getSort().stream()
      .filter(s -> s.getField().getSortFieldName() != null)
      .map(s -> new Sort.Order(s.getDirection(), s.getField().getSortFieldName()))
      .collect(Collectors.toList());
    return !sortOrder.isEmpty() ? new Sort(sortOrder) : null;
  }

}
