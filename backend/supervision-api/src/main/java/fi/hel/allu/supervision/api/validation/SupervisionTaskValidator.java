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

  @Autowired
  private final ApplicationService applicationService;
  @Autowired
  private final UserService userService;

  private final MessageSourceAccessor accessor;

  private static final String ERROR_APPLICATION = "supervisiontask.application.invalid";
  private static final String ERROR_OWNER = "supervisiontask.owner.invalid";

  @Autowired
  SupervisionTaskValidator(
      ApplicationService applicationService,
      UserService userService,
      MessageSource validationMessageSource) {
    this.applicationService= applicationService;
    this.userService = userService;
    accessor = new MessageSourceAccessor(validationMessageSource);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return SupervisionTaskCreateJson.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    SupervisionTaskCreateJson task = (SupervisionTaskCreateJson) target;
    if (!hasValidOwner(task)) {
      errors.rejectValue("ownerId", ERROR_OWNER, accessor.getMessage(ERROR_OWNER));
    }
    if (!hasValidApplication(task)) {
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

  private boolean hasValidOwner(SupervisionTaskCreateJson task) {
    try {
      userService.findUserById(task.getOwnerId());
    } catch (NoSuchEntityException ex) {
      return false;
    }
    return true;
  }
}
