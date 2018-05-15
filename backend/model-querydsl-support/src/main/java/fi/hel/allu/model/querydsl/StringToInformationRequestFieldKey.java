package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;

import fi.hel.allu.common.domain.types.InformationRequestFieldKey;

public class StringToInformationRequestFieldKey extends EnumAsObjectType<InformationRequestFieldKey> {

  public StringToInformationRequestFieldKey() {
    super(InformationRequestFieldKey.class);
  }

}
