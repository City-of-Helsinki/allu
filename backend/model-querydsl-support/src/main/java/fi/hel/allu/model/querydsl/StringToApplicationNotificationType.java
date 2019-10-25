package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;

import fi.hel.allu.common.types.ApplicationNotificationType;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToApplicationNotificationType extends EnumAsObjectType<ApplicationNotificationType> {

  public StringToApplicationNotificationType() {
    super(ApplicationNotificationType.class);
  }
}
