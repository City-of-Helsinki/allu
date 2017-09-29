package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToSupervisionTaskStatusType extends EnumAsObjectType<SupervisionTaskStatusType> {

  public StringToSupervisionTaskStatusType() {
    super(SupervisionTaskStatusType.class);
  }
}
