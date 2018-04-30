package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service for populating <code>ApplicationJson</code>s.
 */
@Service
public class ApplicationJsonService {

  private final ApplicationMapper applicationMapper;
  private final ProjectService projectService;
  private final UserService userService;
  private final LocationService locationService;
  private final AttachmentService attachmentService;
  private final CommentService commentService;

  @Autowired
  public ApplicationJsonService(
      ApplicationMapper applicationMapper,
      ProjectService projectService,
      UserService userService,
      LocationService locationService,
      AttachmentService attachmentService,
      CommentService commentService
  ) {
    this.applicationMapper = applicationMapper;
    this.projectService = projectService;
    this.userService = userService;
    this.locationService = locationService;
    this.attachmentService = attachmentService;
    this.commentService = commentService;
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
    applicationJson.setComments(commentService.findByApplicationId(applicationModel.getId()));

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

    json.setLocations(locationService.findLocationsByApplication(model.getId()));

    return json;
  }
}
