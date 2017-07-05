package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.common.domain.types.ApplicationType;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToApplicationType extends EnumAsObjectType<ApplicationType> {

  public StringToApplicationType() {
    super(ApplicationType.class);
  }
}
