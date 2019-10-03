package fi.hel.allu.supervision.api.service;

import java.time.ZonedDateTime;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.RequiredTasks;
import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.StatusChangeInfoJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.event.ApplicationArchiveEvent;
import fi.hel.allu.servicecore.service.*;
import fi.hel.allu.supervision.api.domain.SupervisionTaskApprovalJson;
import fi.hel.allu.supervision.api.domain.SupervisionTaskRejectionJson;

@Service
public class SupervisionTaskApprovalService {

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;
  @Autowired
  private ApplicationService applicationService;
  @Autowired
  private DateReportingService dateReportingService;
  @Autowired
  private SupervisionTaskService supervisionTaskService;
  @Autowired
  private ApprovalDocumentService approvalDocumentService;
  @Autowired
  private ChargeBasisService chargeBasisService;
  @Autowired
  private ApplicationEventPublisher applicationEventPublisher;
  @Autowired
  private CommentService commentService;
  @Autowired
  private ExcavationAnnouncementService excavationAnnouncementService;

  public SupervisionTaskJson approveSupervisionTask(SupervisionTaskApprovalJson approvalData) {
    SupervisionTaskJson task = supervisionTaskService.findById(approvalData.getTaskId());
    Application application = applicationService.findApplicationById(task.getApplicationId());
    validateApproval(task, application);
    setRequiredTasks(application, task, approvalData);
    return approve(approvalData, task, application);
  }

  private void setRequiredTasks(Application application, SupervisionTaskJson task,
      SupervisionTaskApprovalJson approvalData) {
    if (application.getType() == ApplicationType.EXCAVATION_ANNOUNCEMENT &&
        task.getType() == SupervisionTaskType.PRELIMINARY_SUPERVISION) {
      excavationAnnouncementService.setRequiredTasks(application.getId(),
          new RequiredTasks(
              BooleanUtils.isTrue(approvalData.getCompactionAndBearingCapacityMeasurement()),
              BooleanUtils.isTrue(approvalData.getQualityAssuranceTest())));
    }
  }

  private SupervisionTaskJson approve(SupervisionTaskApprovalJson approvalData, SupervisionTaskJson task, Application application) {
    SupervisionTaskJson result;
    task.setResult(approvalData.getResult());
    if (task.getType() == SupervisionTaskType.OPERATIONAL_CONDITION && application.getType() == ApplicationType.EXCAVATION_ANNOUNCEMENT) {
      result = handleOperationalConditionApproval(approvalData, task, application);
    } else if (task.getType() == SupervisionTaskType.FINAL_SUPERVISION && (application.getType() == ApplicationType.EXCAVATION_ANNOUNCEMENT || application.getType() == ApplicationType.AREA_RENTAL)) {
      result = handleFinalSupervisionApproval(approvalData, task, application);
    } else {
      result = supervisionTaskService.approve(task);
    }
    return result;
  }

  private SupervisionTaskJson handleOperationalConditionApproval(SupervisionTaskApprovalJson approvalData, SupervisionTaskJson task,
      Application application) {
    OperationalConditionDates extension = (OperationalConditionDates)application.getExtension();
    StatusType newStatus = requiresDecision(approvalData.getOperationalConditionDate(), extension.getWinterTimeOperation(), application)
            ? StatusType.DECISIONMAKING
            : StatusType.OPERATIONAL_CONDITION;
    SupervisionTaskJson approved = supervisionTaskService.approve(task);
    dateReportingService.reportOperationalCondition(application.getId(), approvalData.getOperationalConditionDate());
    updateStatus(application.getId(), newStatus, approvalData);
    return approved;
  }

  private SupervisionTaskJson handleFinalSupervisionApproval(SupervisionTaskApprovalJson approvalData, SupervisionTaskJson task, Application application) {
    StatusType newStatus = requiresDecision(approvalData.getWorkFinishedDate(), application.getEndTime(), application)
        ? StatusType.DECISIONMAKING
        : StatusType.FINISHED;
    SupervisionTaskJson approved = supervisionTaskService.approve(task);
    dateReportingService.reportWorkFinished(application.getId(), approvalData.getWorkFinishedDate());
    updateStatus(application.getId(), newStatus, approvalData);
    return approved;
  }

  private boolean requiresDecision(ZonedDateTime actualDate, ZonedDateTime originalDate, Application application) {
    return !TimeUtil.isSameDate(actualDate, originalDate) || application.isInvoicingChanged();
  }

  private void validateApproval(SupervisionTaskJson task, Application application) {
    if (hasTagBlockingApproval(application) || hasStatusBlockingApproval(task, application)) {
      throw new IllegalOperationException("supervisiontask.invalid.applicationstatus");
    }
  }

  private boolean hasStatusBlockingApproval(SupervisionTaskJson task, Application application) {
    return application.getStatus().isBeforeDecision()
        && (task.getType() == SupervisionTaskType.OPERATIONAL_CONDITION || task.getType() == SupervisionTaskType.FINAL_SUPERVISION);
  }

  private boolean hasTagBlockingApproval(Application application) {
    return application.getApplicationTags()
        .stream()
        .anyMatch(t -> t.getType() == ApplicationTagType.DATE_CHANGE || t.getType() == ApplicationTagType.OTHER_CHANGES);
  }

  public SupervisionTaskJson rejectSupervisionTask(Integer id, SupervisionTaskRejectionJson rejectionData) {
    SupervisionTaskJson task = supervisionTaskService.findById(id);
    task.setResult(rejectionData.getResult());
    return supervisionTaskService.reject(task, rejectionData.getNewSupervisionDate());
  }

  private void updateStatus(Integer applicationId, StatusType newStatus, SupervisionTaskApprovalJson approvalData) {
    if (newStatus == StatusType.DECISIONMAKING) {
      StatusChangeInfoJson statusChangeInfo = new StatusChangeInfoJson(approvalData.getDecisionMakerId());
      statusChangeInfo.setComment(approvalData.getDecisionNote());
      statusChangeInfo.setType(CommentType.PROPOSE_APPROVAL);
      commentService.addDecisionProposalComment(applicationId, statusChangeInfo);
      applicationServiceComposer.changeStatus(applicationId, newStatus, statusChangeInfo);
    } else {
      ApplicationJson origApplicationJson = applicationServiceComposer.findApplicationById(applicationId);
      List<ChargeBasisEntry> chargeBasisEntries = chargeBasisService.getUnlockedAndInvoicableChargeBasis(applicationId);
      ApplicationJson applicationJson = applicationServiceComposer.changeStatus(applicationId, newStatus);
      approvalDocumentService.createFinalApprovalDocument(origApplicationJson, applicationJson, chargeBasisEntries);
      if (newStatus == StatusType.FINISHED) {
        applicationEventPublisher.publishEvent(new ApplicationArchiveEvent(applicationId));
      }
    }
  }
}
