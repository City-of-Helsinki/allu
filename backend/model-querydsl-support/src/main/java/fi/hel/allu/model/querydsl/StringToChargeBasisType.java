package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.common.types.ChargeBasisType;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToChargeBasisType extends EnumAsObjectType<ChargeBasisType> {
  public StringToChargeBasisType() {
    super(ChargeBasisType.class);
  }
}
