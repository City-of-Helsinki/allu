package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;

import fi.hel.allu.common.domain.types.ContractStatusType;

public class StringToContractStatus extends EnumAsObjectType<ContractStatusType> {

  public StringToContractStatus() {
      super(ContractStatusType.class);
  }
}
