package fi.hel.allu.supervision.api.service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;

@Service
public class ApplicationUpdateService extends ModelFieldUpdater {

  private ApplicationServiceComposer applicationServiceComposer;

  private LocationUpdateService locationUpdateService;

  public ApplicationUpdateService(ApplicationServiceComposer applicationServiceComposer, LocationUpdateService locationUpdateService) {
    this.applicationServiceComposer = applicationServiceComposer;
    this.locationUpdateService = locationUpdateService;
  }


  public ApplicationJson update(Integer id, Integer version, Map<String, Object> fields) {
    validateUpdateAllowed(id);
    ApplicationJson application = applicationServiceComposer.findApplicationById(id);
    if(application.isNotAreaRental() && application.getLocations().size() == 1  ) {
      updateLocation(fields, application.getLocations().get(0).getId());
      application = applicationServiceComposer.findApplicationById(id);
      }
    updateApplication(application, fields);
    application.setVersion(version);
    return applicationServiceComposer.updateApplication(id, application);
  }

  private void updateApplication(ApplicationJson application, Map<String, Object> fields) {
    Map<String, Object> applicationFields = new HashMap<>();
    Map<String, Object> extensionFields = new HashMap<>();
    fields.keySet().forEach(f ->  {
      if (hasField(f, application)) {
        applicationFields.put(f, fields.get(f));
      } else {
        extensionFields.put(f,  fields.get(f));
      }
    });
    updateObject(applicationFields, application);
    updateObject(extensionFields, application.getExtension());
  }

  private void updateLocation(Map<String, Object> applicationFields, Integer id){
    Map<String, Object> collected = applicationFields.entrySet().stream()
      .filter(x -> x.getKey().equals("startTime") || x.getKey().equals("endTime") )
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    locationUpdateService.update(id,collected);
  }

  private <T> boolean hasField(String fieldName, T targetObject) {
    try {
      return PropertyAccessorFactory.forBeanPropertyAccess(targetObject).getPropertyDescriptor(fieldName) != null;
    } catch (InvalidPropertyException ex) {
      return false;
    }
  }

  public void validateUpdateAllowed(Integer id) {
    StatusType status = applicationServiceComposer.getApplicationStatus(id).getStatus();
    if (status != StatusType.NOTE
        && status != StatusType.HANDLING
        && status != StatusType.PRE_RESERVED
        && status != StatusType.RETURNED_TO_PREPARATION
        && (status != StatusType.PENDING || isExternalApplication(id))) {
      throw new IllegalArgumentException("application.applicationStatus.forbidden");
    }
  }

  private boolean isExternalApplication(Integer id) {
    return applicationServiceComposer.getApplicationExternalOwner(id) != null;
  }

}
