package fi.hel.allu.ui.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.ProjectJson;
import fi.hel.allu.ui.mapper.ApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service for populating <code>ApplicationJson</code>s.
 */
@Service
public class ApplicationJsonService {

  private ApplicationMapper applicationMapper;
  private ProjectService projectService;
  private ApplicantService applicantService;
  private ContactService contactService;
  private MetaService metaService;
  private UserService userService;
  private LocationService locationService;
  private AttachmentService attachmentService;
  private CommentService commentService;

  @Autowired
  public ApplicationJsonService(
      ApplicationMapper applicationMapper,
      ProjectService projectService,
      ApplicantService applicantService,
      ContactService contactService,
      MetaService metaService,
      UserService userService,
      LocationService locationService,
      AttachmentService attachmentService,
      CommentService commentService
  ) {
    this.applicationMapper = applicationMapper;
    this.projectService = projectService;
    this.applicantService = applicantService;
    this.contactService = contactService;
    this.metaService = metaService;
    this.userService = userService;
    this.locationService = locationService;
    this.attachmentService = attachmentService;
    this.commentService = commentService;
  }

  /**
   * Returns fully populated application json i.e. having all related data structures like applicant and project populated.
   *
   * @param   applicationModel  Application to be mapped to fully populated application json.
   * @return  fully populated application json.
   */
  public ApplicationJson getFullyPopulatedApplication(Application applicationModel) {
    ApplicationJson applicationJson = applicationMapper.mapApplicationToJson(applicationModel);

    if (applicationModel.getProjectId() != null) {
      List<ProjectJson> projects = projectService.findByIds(Collections.singletonList(applicationModel.getProjectId()));
      if (projects.size() != 1) {
        throw new NoSuchEntityException("Project linked to application not found!", applicationModel.getProjectId().toString());
      }
      applicationJson.setProject(projects.get(0));
    }
    applicationJson.setApplicant(applicantService.findApplicantById(applicationModel.getApplicantId()));
    applicationJson.setContactList(contactService.findContactsForApplication(applicationModel.getId()));
    applicationJson.setHandler(applicationModel.getHandler() != null ? userService.findUserById(applicationModel.getHandler()) : null);

    applicationJson.setLocations(locationService.findLocationsByApplication(applicationModel.getId()));
    applicationJson.setAttachmentList(attachmentService.findAttachmentsForApplication(applicationModel.getId()));
    applicationJson.setComments(commentService.findByApplicationId(applicationModel.getId()));

    return applicationJson;
  }
}
