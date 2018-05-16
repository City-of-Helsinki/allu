package fi.hel.allu.servicecore.mapper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.hel.allu.servicecore.domain.ApplicationJson;

public class ApplicationJsonMapper {

  public static String getApplicationAsJson(ApplicationJson originalApplication) throws JsonProcessingException {
    return getMapper().writeValueAsString(originalApplication);
  }

  public static ApplicationJson getApplicationFromJson(String applicationJson) throws IOException {
    return getMapper().readValue(applicationJson, ApplicationJson.class);
  }

  private static ObjectMapper getMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }

}
