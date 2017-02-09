package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;

import fi.hel.allu.common.types.ChangeType;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToChangeType extends EnumAsObjectType<ChangeType> {

  public StringToChangeType() {
    super(ChangeType.class);
  }
}
