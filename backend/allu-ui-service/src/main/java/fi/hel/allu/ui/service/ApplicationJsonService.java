package fi.hel.allu.ui.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.AttachmentInfoJson;
import fi.hel.allu.ui.domain.ProjectJson;
import fi.hel.allu.ui.mapper.ApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service for populating <code>ApplicationJson</code>s.
 */
@Service
public class ApplicationJsonService {

  private RestTemplate restTemplate;
  private ApplicationProperties applicationProperties;
  private ApplicationMapper applicationMapper;
  private ProjectService projectService;
  private ApplicantService applicantService;
  private ContactService contactService;
  private MetaService metaService;
  private UserService userService;
  private LocationService locationService;

  @Autowired
  public ApplicationJsonService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      ApplicationMapper applicationMapper,
      ProjectService projectService,
      ApplicantService applicantService,
      ContactService contactService,
      MetaService metaService,
      UserService userService,
      LocationService locationService
  ) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.applicationMapper = applicationMapper;
    this.projectService = projectService;
    this.applicantService = applicantService;
    this.contactService = contactService;
    this.metaService = metaService;
    this.userService = userService;
    this.locationService = locationService;
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
    applicationJson.setMetadata(metaService.findMetadataForApplication(applicationModel.getType(), applicationModel.getMetadataVersion()));
    applicationJson.setHandler(applicationModel.getHandler() != null ? userService.findUserById(applicationModel.getHandler()) : null);

    if (applicationModel.getLocationId() != null && applicationModel.getLocationId() > 0) {
      applicationJson.setLocation(locationService.findLocationById(applicationModel.getLocationId()));
    }
    applicationJson.setAttachmentList(findAttachmentsForApplication(applicationModel.getId()));
    return applicationJson;
  }

  private List<AttachmentInfoJson> findAttachmentsForApplication(Integer applicationId) {
    List<AttachmentInfoJson> resultList = new ArrayList<>();
    ResponseEntity<AttachmentInfo[]> attachmentResult = restTemplate.getForEntity(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_ATTACHMENTS_BY_APPLICATION),
        AttachmentInfo[].class,
        applicationId);
    for (AttachmentInfo attachmentInfo : attachmentResult.getBody()) {
      AttachmentInfoJson attachmentInfoJson = new AttachmentInfoJson();
      applicationMapper.mapAttachmentInfoToJson(attachmentInfoJson, attachmentInfo);
      resultList.add(attachmentInfoJson);
    }
    return resultList;
  }
}
