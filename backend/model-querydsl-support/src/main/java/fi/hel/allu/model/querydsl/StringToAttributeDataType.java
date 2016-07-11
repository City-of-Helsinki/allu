package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.model.domain.meta.AttributeDataType;

/**
 * QueryDSL mapping for mapping AttributeDataType as string in database and vice versa.
 */
public class StringToAttributeDataType extends EnumAsObjectType<AttributeDataType> {
  public StringToAttributeDataType() {
    super(AttributeDataType.class);
  }
}
