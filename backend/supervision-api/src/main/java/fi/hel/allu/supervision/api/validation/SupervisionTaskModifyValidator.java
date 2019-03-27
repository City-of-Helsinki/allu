package fi.hel.allu.supervision.api.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.servicecore.service.UserService;
import fi.hel.allu.supervision.api.domain.SupervisionTaskModifyJson;

@Component
public class SupervisionTaskModifyValidator implements Validator {

  private final UserService userService;
  private final MessageSourceAccessor accessor;

  private static final String ERROR_OWNER = "supervisiontask.owner.invalid";

  @Autowired
  SupervisionTaskModifyValidator(
      UserService userService,
      MessageSource validationMessageSource) {
    this.userService = userService;
    accessor = new MessageSourceAccessor(validationMessageSource);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return SupervisionTaskModifyJson.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    SupervisionTaskModifyJson task = (SupervisionTaskModifyJson) target;
    if (task.getOwnerId() != null && !hasValidOwner(task)) {
      errors.rejectValue("ownerId", ERROR_OWNER, accessor.getMessage(ERROR_OWNER));
    }
  }

  private boolean hasValidOwner(SupervisionTaskModifyJson task) {
    try {
      userService.findUserById(task.getOwnerId());
    } catch (NoSuchEntityException ex) {
      return false;
    }
    return true;
  }
}
