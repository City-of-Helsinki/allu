package fi.hel.allu.supervision.api.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.service.UserService;
import fi.hel.allu.supervision.api.domain.DecisionInfo;

@Component
public class DecisionMakerValidator implements Validator {
  private final UserService userService;
  private final MessageSourceAccessor accessor;
  private static final String ERROR_DECISION_MAKER_INVALID = "decision.maker.invalid";

  @Autowired
  DecisionMakerValidator(
      UserService userService,
      MessageSource validationMessageSource) {
    this.userService = userService;
    accessor = new MessageSourceAccessor(validationMessageSource);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return DecisionInfo.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    DecisionInfo info = (DecisionInfo)target;
    if (info.getDecisionMakerId() != null && !isValidDecisionMaker(info.getDecisionMakerId())) {
      errors.rejectValue("decisionMakerId", ERROR_DECISION_MAKER_INVALID, accessor.getMessage(ERROR_DECISION_MAKER_INVALID));
    }
  }

  private boolean isValidDecisionMaker(Integer decisionMakerId) {
    try {
      UserJson user = userService.findUserById(decisionMakerId);
      return user.getAssignedRoles().contains(RoleType.ROLE_DECISION);
    } catch (NoSuchEntityException ex) {
      return false;
    }
  }
}