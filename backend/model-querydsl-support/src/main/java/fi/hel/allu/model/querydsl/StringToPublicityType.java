package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.common.types.PublicityType;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToPublicityType extends EnumAsObjectType<PublicityType> {

  public StringToPublicityType() {
    super(PublicityType.class);
  }

}
