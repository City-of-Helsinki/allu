package fi.hel.allu.supervision.api.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.service.ApplicationService;
import fi.hel.allu.servicecore.service.SupervisionTaskService;
import fi.hel.allu.servicecore.service.UserService;
import fi.hel.allu.supervision.api.domain.SupervisionTaskApprovalJson;

@Component
public class SupervisionTaskApprovalValidator implements Validator {

  private final SupervisionTaskService supervisionTaskService;
  private final ApplicationService applicationService;
  private final UserService userService;
  private final MessageSourceAccessor accessor;

  private static final String ERROR_OPERATIONAL_DATE = "supervisiontask.operational.date";
  private static final String ERROR_FINAL_DATE = "supervisiontask.finalSupervision.date";
  private static final String ERROR_STATUS = "supervisiontask.invalid.taskstatus";
  private static final String ERROR_DECISION_NOTE = "supervisiontask.decision.note";
  private static final String ERROR_DECISION_MAKER_REQUIRED = "supervisiontask.decision.maker.required";
  private static final String ERROR_DECISION_MAKER_INVALID = "supervisiontask.decision.maker.invalid";

  @Autowired
  SupervisionTaskApprovalValidator(
      SupervisionTaskService supervisionTaskService,
      ApplicationService applicationService,
      UserService userService,
      MessageSource validationMessageSource) {
    this.supervisionTaskService = supervisionTaskService;
    this.applicationService = applicationService;
    this.userService = userService;
    accessor = new MessageSourceAccessor(validationMessageSource);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return SupervisionTaskApprovalJson.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    SupervisionTaskApprovalJson approval = (SupervisionTaskApprovalJson)target;
    if (approval.getTaskId() != null) {
      SupervisionTaskJson task = supervisionTaskService.findById(approval.getTaskId());
      Application application = applicationService.findApplicationById(task.getApplicationId());
      validateTaskStatus(task, errors);
      validateOperationalConditionDate(approval, task, errors);
      validateWorkFinishedDate(approval, task, application, errors);
      validateDecisionData(approval, task, application, errors);
    }
  }

  private void validateTaskStatus(SupervisionTaskJson task, Errors errors) {
    if (task.getStatus() != SupervisionTaskStatusType.OPEN) {
      errors.rejectValue("taskId", ERROR_STATUS, accessor.getMessage(ERROR_STATUS));
    }
  }

  private void validateOperationalConditionDate(SupervisionTaskApprovalJson approval, SupervisionTaskJson task,
      Errors errors) {
    if (approval.getOperationalConditionDate() == null && operationlConditionDateRequired(task)) {
      errors.rejectValue("operationalConditionDate", ERROR_OPERATIONAL_DATE, accessor.getMessage(ERROR_OPERATIONAL_DATE));
    }
  }

  private void validateWorkFinishedDate(SupervisionTaskApprovalJson approval, SupervisionTaskJson task, Application application,
      Errors errors) {
    if (approval.getWorkFinishedDate() == null && workFinishedDateRequired(task, application) ) {
      errors.rejectValue("workFinishedDate", ERROR_FINAL_DATE, accessor.getMessage(ERROR_FINAL_DATE));
    }
  }

  private void validateDecisionData(SupervisionTaskApprovalJson approval, SupervisionTaskJson task, Application application,
      Errors errors) {
    if (requiresDecisionMakingData(task, application)) {
      validateDecisionMaker(approval.getDecisionMakerId(), errors);
      if (StringUtils.isBlank(approval.getDecisionNote())) {
        errors.rejectValue("decisionNote", ERROR_DECISION_NOTE, accessor.getMessage(ERROR_DECISION_NOTE));
      }
    }
  }

  private void validateDecisionMaker(Integer decisionMakerId, Errors errors) {
    if (decisionMakerId == null) {
      errors.rejectValue("decisionMakerId", ERROR_DECISION_MAKER_REQUIRED, accessor.getMessage(ERROR_DECISION_MAKER_REQUIRED));
    } else if (!isValidDecisionMaker(decisionMakerId)) {
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

  private boolean requiresDecisionMakingData(SupervisionTaskJson task, Application application) {
    return workFinishedDateRequired(task, application) || operationlConditionDateRequired(task);
  }

  private boolean workFinishedDateRequired(SupervisionTaskJson task, Application application) {
    return task.getType() == SupervisionTaskType.FINAL_SUPERVISION
        && (application.getType() == ApplicationType.AREA_RENTAL
            || application.getType() == ApplicationType.EXCAVATION_ANNOUNCEMENT);
  }

  private boolean operationlConditionDateRequired(SupervisionTaskJson task) {
    return task.getType() == SupervisionTaskType.OPERATIONAL_CONDITION;
  }
}
