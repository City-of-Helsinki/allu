package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.common.domain.types.CodeSetType;

public class StringToCodeSetType extends EnumAsObjectType<CodeSetType> {
  public StringToCodeSetType() {
    super(CodeSetType.class);
  }
}
