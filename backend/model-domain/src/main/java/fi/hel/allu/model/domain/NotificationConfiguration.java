package fi.hel.allu.model.domain;

import java.util.Arrays;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.ApplicationNotificationType;

public class NotificationConfiguration {

  private ApplicationNotificationType notificationType;
  private String[] applicationStates;
  private String[] specifiers;

  public NotificationConfiguration() {
  }

  public ApplicationNotificationType getNotificationType() {
    return notificationType;
  }

  public void setNotificationType(ApplicationNotificationType notificationType) {
    this.notificationType = notificationType;
  }

  public String[] getApplicationStates() {
    return applicationStates;
  }

  public void setApplicationStates(String[] applicationStates) {
    this.applicationStates = applicationStates;
  }

  public String[] getSpecifiers() {
    return specifiers;
  }

  public void setSpecifiers(String[] specifiers) {
    this.specifiers = specifiers;
  }

  public boolean matches(ApplicationNotificationType type, StatusType status, String specifier) {
    return type == this.notificationType && statusMatches(status) && specifierMatches(specifier);

  }

  private boolean statusMatches(StatusType status) {
    return this.applicationStates.length == 0 || Arrays.asList(applicationStates).contains(status.name());
  }

  private boolean specifierMatches(String specifier) {
    return this.specifiers.length == 0 || Arrays.asList(specifiers).contains(specifier);
  }

}
