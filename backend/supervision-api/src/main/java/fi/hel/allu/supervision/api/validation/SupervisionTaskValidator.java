package fi.hel.allu.supervision.api.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.servicecore.service.ApplicationService;
import fi.hel.allu.servicecore.service.UserService;
import fi.hel.allu.supervision.api.domain.SupervisionTaskCreateJson;

@Component
public class SupervisionTaskValidator implements Validator {

  private final ApplicationService applicationService;
  private final MessageSourceAccessor accessor;

  private static final String ERROR_APPLICATION = "supervisiontask.application.invalid";

  @Autowired
  SupervisionTaskValidator(
      ApplicationService applicationService,
      MessageSource validationMessageSource) {
    this.applicationService = applicationService;
    accessor = new MessageSourceAccessor(validationMessageSource);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return SupervisionTaskCreateJson.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    SupervisionTaskCreateJson task = (SupervisionTaskCreateJson) target;
    if (task.getApplicationId() != null && !hasValidApplication(task)) {
      errors.rejectValue("applicationId", ERROR_APPLICATION, accessor.getMessage(ERROR_APPLICATION));
    }
  }

  private boolean hasValidApplication(SupervisionTaskCreateJson task) {
    try {
      applicationService.findApplicationById(task.getApplicationId());
    } catch (NoSuchEntityException ex) {
      return false;
    }
    return true;
  }
}
