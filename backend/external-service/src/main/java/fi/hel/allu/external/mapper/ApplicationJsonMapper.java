package fi.hel.allu.external.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.hel.allu.servicecore.domain.ApplicationJson;

public class ApplicationJsonMapper {

  public static String getApplicationAsJson(ApplicationJson originalApplication) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String json = mapper.writeValueAsString(originalApplication);
    return json;
  }
}
