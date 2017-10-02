package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;

import fi.hel.allu.model.domain.ChargeBasisUnit;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToChargeBasisUnit extends EnumAsObjectType<ChargeBasisUnit> {

  public StringToChargeBasisUnit() {
    super(ChargeBasisUnit.class);
  }
}
