package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;

import java.time.ZonedDateTime;
import java.util.List;

public interface ApplicationInterface {

    Integer getId();

    void setId(Integer id);

    StatusType getStatus();
    String getApplicationId();
    String getName();
    ZonedDateTime getCreationTime();
    ZonedDateTime getReceivedTime();
    ZonedDateTime getStartTime();
    ZonedDateTime getEndTime();

    ZonedDateTime getRecurringEndTime();
    ApplicationType getType();
    <V extends ApplicationTagInterface> List<V> getApplicationTags();
    ZonedDateTime getDecisionTime();
    Object getExtension();
    <U extends LocationInterface> List<U> getLocations();
    <T extends CustomerWithContactsI> List<T> getCustomersWithContacts();

    String getIdentificationNumber();
    Boolean getOwnerNotification();
}