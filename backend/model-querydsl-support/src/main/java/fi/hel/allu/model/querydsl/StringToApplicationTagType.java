package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.common.domain.types.ApplicationTagType;

/**
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToApplicationTagType extends EnumAsObjectType<ApplicationTagType> {

  public StringToApplicationTagType() {
    super(ApplicationTagType.class);
  }
}
