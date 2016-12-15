package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;

import fi.hel.allu.common.types.CableInfoType;

/*
 * Tells QueryDSL how to map CableInfoType to SQL
 */
public class StringToCableInfoType extends EnumAsObjectType<CableInfoType> {
  public StringToCableInfoType() {
    super(CableInfoType.class);
  }
}

