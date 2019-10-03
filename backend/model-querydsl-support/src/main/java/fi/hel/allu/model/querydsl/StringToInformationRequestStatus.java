package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;

import fi.hel.allu.common.domain.types.InformationRequestStatus;

public class StringToInformationRequestStatus extends EnumAsObjectType<InformationRequestStatus> {

  public StringToInformationRequestStatus() {
    super(InformationRequestStatus.class);
  }

}
