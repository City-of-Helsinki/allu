package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToSupervisionTaskType extends EnumAsObjectType<SupervisionTaskType> {

  public StringToSupervisionTaskType() {
    super(SupervisionTaskType.class);
  }
}
