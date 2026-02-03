package fi.hel.allu.model.querydsl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.querydsl.sql.types.AbstractType;
import fi.hel.allu.model.domain.ApplicationExtension;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class StringToApplicationExtension extends AbstractType<ApplicationExtension> {

  public StringToApplicationExtension() {
    super(Types.OTHER);
  }

  @Override
  public Class<ApplicationExtension> getReturnedClass() {
    return ApplicationExtension.class;
  }

  @Nullable
  @Override
  public ApplicationExtension getValue(ResultSet resultSet, int i) throws SQLException {
    try {
      String jsonString = resultSet.getString(i);
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      return mapper.readValue(jsonString, ApplicationExtension.class);
    } catch (IOException ex) {
      throw new SQLException(ex);
    }
  }

  @Override
  public void setValue(PreparedStatement preparedStatement, int i, ApplicationExtension applicationExtension) throws SQLException {
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      preparedStatement.setString(i, mapper.writeValueAsString(applicationExtension));
    } catch (JsonProcessingException ex) {
      throw new SQLException(ex);
    }
  }
}
