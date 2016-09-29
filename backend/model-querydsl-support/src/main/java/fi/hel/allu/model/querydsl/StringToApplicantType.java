package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;

import fi.hel.allu.common.types.ApplicantType;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToApplicantType extends EnumAsObjectType<ApplicantType> {

  public StringToApplicantType() {
    super(ApplicantType.class);
  }

}
