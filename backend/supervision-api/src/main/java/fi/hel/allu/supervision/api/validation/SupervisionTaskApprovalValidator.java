package fi.hel.allu.supervision.api.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.service.ApplicationService;
import fi.hel.allu.servicecore.service.SupervisionTaskService;
import fi.hel.allu.supervision.api.domain.SupervisionTaskApprovalJson;

@Component
public class SupervisionTaskApprovalValidator implements Validator {

  private final SupervisionTaskService supervisionTaskService;
  private final ApplicationService applicationService;
  private final MessageSourceAccessor accessor;

  private static final String ERROR_OPERATIONAL_DATE = "supervisiontask.operational.date";
  private static final String ERROR_FINAL_DATE = "supervisiontask.finalSupervision.date";
  private static final String ERROR_STATUS = "supervisiontask.invalid.taskstatus";

  @Autowired
  SupervisionTaskApprovalValidator(
      SupervisionTaskService supervisionTaskService,
      ApplicationService applicationService,
      MessageSource validationMessageSource) {
    this.supervisionTaskService = supervisionTaskService;
    this.applicationService = applicationService;
    accessor = new MessageSourceAccessor(validationMessageSource);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return SupervisionTaskApprovalJson.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    SupervisionTaskApprovalJson approval = (SupervisionTaskApprovalJson)target;
    SupervisionTaskJson task = supervisionTaskService.findById(approval.getTaskId());
    Application application = applicationService.findApplicationById(task.getApplicationId());
    if (task.getStatus() != SupervisionTaskStatusType.OPEN) {
      errors.rejectValue("taskId", ERROR_STATUS, accessor.getMessage(ERROR_STATUS));
    }
    if (approval.getOperationalConditionDate() == null && operationlConditionDateRequired(task)) {
      errors.rejectValue("operationalConditionDate", ERROR_OPERATIONAL_DATE, accessor.getMessage(ERROR_OPERATIONAL_DATE));
    }
    if (approval.getWorkFinishedDate() == null && workFinishedDateRequired(task, application) ) {
      errors.rejectValue("workFinishedDate", ERROR_FINAL_DATE, accessor.getMessage(ERROR_FINAL_DATE));
    }
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
