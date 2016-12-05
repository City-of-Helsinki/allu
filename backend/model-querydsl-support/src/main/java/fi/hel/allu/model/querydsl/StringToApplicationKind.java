package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.common.types.ApplicationKind;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToApplicationKind extends EnumAsObjectType<ApplicationKind> {

  public StringToApplicationKind() {
    super(ApplicationKind.class);
  }
}
