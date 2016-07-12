package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.common.types.StatusType;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToStatusType extends EnumAsObjectType<StatusType> {
  public StringToStatusType() {
    super(StatusType.class);
  }
}
