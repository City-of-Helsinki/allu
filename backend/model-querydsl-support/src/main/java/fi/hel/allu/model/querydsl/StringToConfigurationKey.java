package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.model.domain.ConfigurationKey;

public class StringToConfigurationKey extends EnumAsObjectType<ConfigurationKey> {

  public StringToConfigurationKey() {
    super(ConfigurationKey.class);
  }
}
