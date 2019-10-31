package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.util.Printable;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ApplicationMapItemJson;
import fi.hel.allu.servicecore.domain.FixedLocationJson;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.mapper.LocationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for populating <code>ApplicationJson</code>s.
 */
@Service
public class ApplicationJsonService {

  private final ApplicationMapper applicationMapper;
  private final ProjectService projectService;
  private final UserService userService;
  private final AttachmentService attachmentService;
  private final CommentService commentService;
  private final LocationService locationService;
  private final TerminationService terminationService;
  private volatile Map<Integer, FixedLocationJson> fixedLocations;

  @Autowired
  public ApplicationJsonService(
      ApplicationMapper applicationMapper,
      ProjectService projectService,
      UserService userService,
      LocationService locationService,
      AttachmentService attachmentService,
      CommentService commentService,
      TerminationService terminationService) {
    this.applicationMapper = applicationMapper;
    this.projectService = projectService;
    this.userService = userService;
    this.attachmentService = attachmentService;
    this.commentService = commentService;
    this.locationService = locationService;
    this.terminationService = terminationService;
    this.fixedLocations = null;
  }

  /**
   * Returns fully populated application json i.e. having all related data structures like customer and project populated.
   *
   * @param   applicationModel  Application to be mapped to fully populated application json.
   * @return  fully populated application json.
   */
  public ApplicationJson getFullyPopulatedApplication(Application applicationModel) {
    ApplicationJson applicationJson = populateCommon(applicationModel);

    applicationJson.setHandler(applicationModel.getHandler() != null ? userService.findUserById(applicationModel.getHandler()) : null);
    applicationJson.setAttachmentList(attachmentService.findAttachmentsForApplication(applicationModel.getId()));

    return applicationJson;
  }

  public ApplicationJson getCompactPopulatedApplication(Application model) {
    return populateCommon(model);
  }

  public ApplicationJson populateCommon(Application model) {
    ApplicationJson json = applicationMapper.mapApplicationToJson(model);

    Optional.ofNullable(model.getProjectId())
        .map(id -> projectService.findById(id))
        .ifPresent(project -> json.setProject(project));

    Optional.ofNullable(model.getOwner())
        .map(owner -> userService.findUserById(owner))
        .ifPresent(owner -> json.setOwner(owner));

    setAddress(json);

    json.setComments(commentService.findByApplicationId(model.getId()));

    json.setTerminationTime(terminationTime(model));

    return json;
  }


  public ApplicationMapItemJson getApplicationMapItemJson(Application model) {
    return new ApplicationMapItemJson(
        model.getId(),
        model.getApplicationId(),
        model.getName(),
        model.getType(),
        model.getStartTime(),
        model.getEndTime(),
        model.getProjectId() != null ? new ProjectJson(model.getProjectId()) : null,
        LocationMapper.mapToLocationJsons(model.getLocations()),
        model.getStatus()
     );
  }

  /**
   * If an application has a fixed location defined then address is name of the fixed location + sections.
   * Otherwise it is street address.
   */
  private void setAddress(ApplicationJson applicationJson) {
    if (applicationJson.getLocations() != null) {
      applicationJson.getLocations().stream().filter((loc) -> (loc.getFixedLocationIds() != null))
        .forEachOrdered((loc) -> {
          String address = "";
          List<FixedLocationJson> fixedLocations = loc.getFixedLocationIds().stream().map(id -> fixedLocations().get(id)).collect(Collectors.toList());
          address = getFixedLocationAddresses(fixedLocations);
          if (!address.isEmpty()) {
            loc.setAddress(address);
          } else {
            loc.setAddress(Optional.ofNullable(loc.getPostalAddress()).map(p -> p.getStreetAddress()).orElse(null));
          }
        }
      );
    }
  }

  private String getFixedLocationAddresses(List<FixedLocationJson> fixedLocations) {
    return Printable.forAreasWithSections(getAreasWithSections(fixedLocations));
  }

  private static Map<String, List<String>> getAreasWithSections(List<FixedLocationJson> fixedLocations) {
    return fixedLocations.stream().
        collect(Collectors.groupingBy(FixedLocationJson::getArea,
            Collectors.mapping(FixedLocationJson::getSection, Collectors.toList())));
  }

  private Map<Integer, FixedLocationJson> fixedLocations() {
    Map<Integer, FixedLocationJson> fixedLocations = this.fixedLocations;
    if (fixedLocations == null) {
      synchronized (ApplicationJsonService.class) {
        fixedLocations = this.fixedLocations;
        if (fixedLocations == null) {
          this.fixedLocations = fixedLocations = locationService.getAllFixedLocations().stream()
              .collect(Collectors.toMap(FixedLocationJson::getId, item -> item));
        }
      }
    }
    return fixedLocations;
  }

  private ZonedDateTime terminationTime(Application application) {
    if (application.getStatus() == StatusType.TERMINATED || application.getStatus() == StatusType.ARCHIVED) {
      return Optional.ofNullable(terminationService.getTerminationInfo(application.getId()))
        .map(TerminationInfo::getExpirationTime)
        .orElse(null);
    }
    return null;
  }
}
