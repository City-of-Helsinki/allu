package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.common.types.AttachmentType;

/**
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToAttachmentType extends EnumAsObjectType<AttachmentType> {

  public StringToAttachmentType() {
    super(AttachmentType.class);
  }
}
