package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.common.domain.types.ExternalRoleType;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToExternalRoleType extends EnumAsObjectType<ExternalRoleType> {

  public StringToExternalRoleType() {
    super(ExternalRoleType.class);
  }
}
