package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;

import fi.hel.allu.common.types.DefaultTextType;

/*
 * Tells QueryDSL how to map DefaultTextType to SQL
 */
public class StringToDefaultTextType extends EnumAsObjectType<DefaultTextType> {
  public StringToDefaultTextType() {
    super(DefaultTextType.class);
  }
}

