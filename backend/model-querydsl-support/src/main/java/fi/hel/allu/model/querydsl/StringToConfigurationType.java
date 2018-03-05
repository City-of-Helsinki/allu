package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.model.domain.ConfigurationType;

public class StringToConfigurationType extends EnumAsObjectType<ConfigurationType> {

  public StringToConfigurationType() {
    super(ConfigurationType.class);
  }
}
