package fi.hel.allu.external.service;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.external.domain.ApplicationProgressReportExt;
import fi.hel.allu.external.domain.ApplicationProgressStatusExt;
import fi.hel.allu.external.domain.InspectionStatus;
import fi.hel.allu.external.domain.InspectionStatus.State;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.CommentService;
import fi.hel.allu.servicecore.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Service class for application-related operations that are only needed in
 * external service.
 */
@Service
public class ApplicationServiceExt {

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;
  @Autowired
  private UserService userService;
  @Autowired
  private CommentService commentService;

  public void reportProgress(int applicationId, ApplicationProgressReportExt applicationProgressReportExt) {
    Optional<ZonedDateTime> workFinished = Optional.ofNullable(applicationProgressReportExt.getWorkFinished());
    Optional<ZonedDateTime> winterTimeOperation = Optional.ofNullable(applicationProgressReportExt.getWinterTimeOperation());
    ApplicationJson applicationJson = applicationServiceComposer.findApplicationById(applicationId);
    ApplicationType applicationType = applicationJson.getType();
    switch (applicationType) {
      case EXCAVATION_ANNOUNCEMENT:
        ExcavationAnnouncementJson excAnn = (ExcavationAnnouncementJson) applicationJson.getExtension();
        winterTimeOperation.ifPresent(o -> excAnn.setWinterTimeOperation(o));
        workFinished.ifPresent(o -> excAnn.setWorkFinished(o));
        break;
      case AREA_RENTAL:
        throwIfPresent(winterTimeOperation, "Winter time operation", applicationType);
        AreaRentalJson areaRental = (AreaRentalJson) applicationJson.getExtension();
        workFinished.ifPresent(o -> areaRental.setWorkFinished(o));
        break;
      case TEMPORARY_TRAFFIC_ARRANGEMENTS:
        throwIfPresent(winterTimeOperation, "Winter time operation", applicationType);
        TrafficArrangementJson trafficArrangement = (TrafficArrangementJson) applicationJson.getExtension();
        workFinished.ifPresent(o -> trafficArrangement.setWorkFinished(o));
        break;
      default:
        throw new IllegalArgumentException("Unsupported application type " + applicationType);
    }
    int userId = userService.getCurrentUser().getId();
    List<ApplicationTagJson> tags = Optional.ofNullable(applicationJson.getApplicationTags())
        .orElseGet(() -> new ArrayList<>());
    if (workFinished.isPresent()) {
      addTagIfNotPresent(tags,
          new ApplicationTagJson(userId, ApplicationTagType.WORK_READY_REPORTED, ZonedDateTime.now()));
    }
    if (winterTimeOperation.isPresent()) {
      addTagIfNotPresent(tags,
          new ApplicationTagJson(userId, ApplicationTagType.OPERATIONAL_CONDITION_REPORTED, ZonedDateTime.now()));
    }
    applicationJson.setApplicationTags(tags);
    applicationServiceComposer.updateApplication(applicationId, applicationJson);
  }

  public ApplicationProgressStatusExt getProgressStatus(int id) {
    ApplicationProgressStatusExt applicationProgressStatusExt = new ApplicationProgressStatusExt(
        new InspectionStatus(State.NOT_INSPECTED, ""), new InspectionStatus(State.NOT_INSPECTED, ""));
    final ApplicationJson applicationJson = applicationServiceComposer.findApplicationById(id);
    final List<CommentJson> comments = commentService.findByApplicationId(id);
    final List<ApplicationTagJson> tags = applicationJson.getApplicationTags();
    if (tags != null) {
      for (ApplicationTagJson tag : tags) {
        switch (tag.getType()) {
          case OPERATIONAL_CONDITION_ACCEPTED:
            applicationProgressStatusExt.getWinterTimeOperationStatus().setState(State.ACCEPTED);
            applicationProgressStatusExt.getWinterTimeOperationStatus()
                .setComment(findNewestComment(CommentType.OPERATIONAL_CONDITION_ACCEPTED, comments));
            break;
          case OPERATIONAL_CONDITION_REJECTED:
            applicationProgressStatusExt.getWinterTimeOperationStatus().setState(State.REJECTED);
            applicationProgressStatusExt.getWinterTimeOperationStatus()
                .setComment(findNewestComment(CommentType.OPERATIONAL_CONDITION_REJECTED, comments));
            break;
          case WORK_READY_ACCEPTED:
            applicationProgressStatusExt.getWorkFinishedStatus().setState(State.ACCEPTED);
            applicationProgressStatusExt.getWinterTimeOperationStatus()
                .setComment(findNewestComment(CommentType.WORK_READY_ACCEPTED, comments));
            break;
          case WORK_READY_REJECTED:
            applicationProgressStatusExt.getWorkFinishedStatus().setState(State.REJECTED);
            applicationProgressStatusExt.getWinterTimeOperationStatus()
                .setComment(findNewestComment(CommentType.WORK_READY_REJECTED, comments));
            break;
          default:
            break;
        }
      }
    }
    return applicationProgressStatusExt;
  }

  private String findNewestComment(CommentType commentType, List<CommentJson> comments) {
    return comments.stream().filter(c -> commentType.equals(c.getType()))
        .sorted((c1, c2) -> c2.getCreateTime().compareTo(c1.getCreateTime())).map(c -> c.getText()).findFirst()
        .orElse(null);
  }

  private void throwIfPresent(Optional<ZonedDateTime> optional, String describe, ApplicationType applicationType) {
    if (optional.isPresent()) {
      throw new IllegalArgumentException(describe + " not applicable for " + applicationType);
    }
  }

  private void addTagIfNotPresent(Collection<ApplicationTagJson> dest, ApplicationTagJson tag) {
    if (dest.stream().noneMatch(t -> t.getType().equals(tag.getType()))) {
      dest.add(tag);
    }
  }
}
