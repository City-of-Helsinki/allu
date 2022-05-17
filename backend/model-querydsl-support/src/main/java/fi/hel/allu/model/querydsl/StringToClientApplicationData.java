package fi.hel.allu.model.querydsl;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.querydsl.sql.types.AbstractType;

import fi.hel.allu.model.domain.ClientApplicationData;
import org.springframework.lang.Nullable;

public class StringToClientApplicationData extends AbstractType<ClientApplicationData> {

  public StringToClientApplicationData() {
    super(Types.VARCHAR);
  }

  @Override
  public Class<ClientApplicationData> getReturnedClass() {
    return ClientApplicationData.class;
  }

  @Nullable
  @Override
  public ClientApplicationData getValue(ResultSet resultSet, int i) throws SQLException {
    try {
      String jsonString = resultSet.getString(i);
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      return jsonString != null ? mapper.readValue(jsonString, ClientApplicationData.class) : null;
    } catch (IOException ex) {
      throw new SQLException(ex);
    }
  }

  @Override
  public void setValue(PreparedStatement preparedStatement, int i, ClientApplicationData clientApplicationData) throws SQLException {
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      preparedStatement.setString(i, clientApplicationData != null ?  mapper.writeValueAsString(clientApplicationData) : null);
    } catch (JsonProcessingException ex) {
      throw new SQLException(ex);
    }
  }
}
