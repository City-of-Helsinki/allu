package fi.hel.allu.servicecore.util;

import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

public class PageRequestBuilder {
  /**
   * Creates uri with query parameters from given base uri and page request
   * @param uri containing base uri as string
   * @param pageRequest contains paging related information
   * @return URI with paging as query parameters
   */
  public static URI fromUriString(String uri, Pageable pageRequest) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri);

    builder.queryParam("page", pageRequest.getPageNumber());
    builder.queryParam("size", pageRequest.getPageSize());
    Optional.ofNullable(pageRequest.getSort()).ifPresent(sort -> sort.forEach(s ->
        builder.queryParam("sort", s.getProperty() + (s.isDescending() ? ",desc" : ",asc"))));

    return builder.build().toUri();
  }
}
