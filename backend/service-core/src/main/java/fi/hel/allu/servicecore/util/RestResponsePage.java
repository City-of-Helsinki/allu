package fi.hel.allu.servicecore.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RestResponsePage<T> extends PageImpl<T> {

  private static final long serialVersionUID = -1106844783610186793L;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public RestResponsePage(@JsonProperty("content") List<T> content,
                      @JsonProperty("number") int page,
                      @JsonProperty("size") int size,
                      @JsonProperty("totalElements") long total) {
      super(content, PageRequest.of(page, size), total);
  }

  public RestResponsePage(List<T> content, Pageable pageable, long total) {
      super(content, pageable, total);
  }

  public RestResponsePage(List<T> content) {
      super(content);
  }

  public RestResponsePage() {
      super(new ArrayList<>());
  }
}