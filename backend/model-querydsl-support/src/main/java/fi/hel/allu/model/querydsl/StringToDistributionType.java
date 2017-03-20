package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.common.types.DistributionType;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToDistributionType extends EnumAsObjectType<DistributionType> {

  public StringToDistributionType() {
    super(DistributionType.class);
  }

}
