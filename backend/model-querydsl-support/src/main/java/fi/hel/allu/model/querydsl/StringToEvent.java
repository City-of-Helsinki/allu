package fi.hel.allu.model.querydsl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.querydsl.sql.types.AbstractType;
import fi.hel.allu.model.domain.Event;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import static fi.hel.allu.common.types.ApplicationType.OutdoorEvent;
import static fi.hel.allu.common.types.ApplicationType.valueOf;

public class StringToEvent extends AbstractType<Event> {

  public StringToEvent() {
    super(Types.VARCHAR);
  }

  @Override
  public Class<Event> getReturnedClass() {
    return Event.class;
  }

  @Nullable
  @Override
  public Event getValue(ResultSet resultSet, int i) throws SQLException {
    try {
      String jsonString = resultSet.getString(i);
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      return mapper.readValue(jsonString, Event.class);
    } catch (IOException ex) {
      throw new SQLException(ex);
    }
  }

  @Override
  public void setValue(PreparedStatement preparedStatement, int i, Event event) throws SQLException {
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      preparedStatement.setString(i, mapper.writeValueAsString(event));
    } catch (JsonProcessingException ex) {
      throw new SQLException(ex);
    }
  }
}
