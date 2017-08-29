package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;

import fi.hel.allu.common.types.ApplicationSpecifier;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToApplicationSpecifier extends EnumAsObjectType<ApplicationSpecifier> {

  public StringToApplicationSpecifier() {
    super(ApplicationSpecifier.class);
  }
}
