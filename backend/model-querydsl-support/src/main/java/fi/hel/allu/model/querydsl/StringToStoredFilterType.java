package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.common.domain.types.StoredFilterType;

public class StringToStoredFilterType extends EnumAsObjectType<StoredFilterType> {

  public StringToStoredFilterType() {
    super(StoredFilterType.class);
  }
}
