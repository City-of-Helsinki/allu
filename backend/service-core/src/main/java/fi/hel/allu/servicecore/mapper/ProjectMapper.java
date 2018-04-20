package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.search.domain.ProjectES;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.servicecore.service.ContactService;
import fi.hel.allu.servicecore.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Mapper for projects in their different forms.
 */
@Component
public class ProjectMapper {

  private final CustomerService customerService;
  private final ContactService contactService;

  @Autowired
  public ProjectMapper(@Lazy CustomerService customerService, @Lazy ContactService contactService) {
    this.customerService = customerService;
    this.contactService = contactService;
  }

  public Project createProjectModel(ProjectJson projectJson) {
    Project projectDomain = new Project();
    projectDomain.setId(projectJson.getId());
    projectDomain.setName(projectJson.getName());
    projectDomain.setStartTime(projectJson.getStartTime());
    projectDomain.setEndTime(projectJson.getEndTime());
    if (projectJson.getCityDistricts() != null) {
      projectDomain.setCityDistricts(projectJson.getCityDistricts().toArray(new Integer[0]));
    }
    projectDomain.setCustomerReference(projectJson.getCustomerReference());
    projectDomain.setAdditionalInfo(projectJson.getAdditionalInfo());
    projectDomain.setParentId(projectJson.getParentId());
    if (projectJson.getCustomer() != null) {
      projectDomain.setCustomerId(projectJson.getCustomer().getId());
    }
    if (projectJson.getContact() != null) {
      projectDomain.setContactId(projectJson.getContact().getId());
    }
    projectDomain.setIdentifier(projectJson.getIdentifier());
    return projectDomain;
  }

  public ProjectJson mapProjectToJson(Project projectDomain) {
    ProjectJson projectJson = new ProjectJson();
    projectJson.setId(projectDomain.getId());
    projectJson.setName(projectDomain.getName());
    projectJson.setStartTime(projectDomain.getStartTime());
    projectJson.setEndTime(projectDomain.getEndTime());
    if (projectDomain.getCityDistricts() != null) {
      projectJson.setCityDistricts(Arrays.asList(projectDomain.getCityDistricts()));
    }
    projectJson.setCustomerReference(projectDomain.getCustomerReference());
    projectJson.setAdditionalInfo(projectDomain.getAdditionalInfo());
    projectJson.setParentId(projectDomain.getParentId());
    if (projectDomain.getCustomerId() != null) {
      projectJson.setCustomer(customerService.findCustomerById(projectDomain.getCustomerId()));
    }
    if (projectDomain.getContactId() != null) {
      projectJson.setContact(contactService.findById(projectDomain.getContactId()));
    }
    projectJson.setIdentifier(projectDomain.getIdentifier());
    return projectJson;
  }

  public ProjectES createProjectESModel(ProjectJson projectJson) {
    ProjectES projectES = new ProjectES();
    projectES.setId(projectJson.getId());
    projectES.setName(projectJson.getName());
    projectES.setStartTime(TimeUtil.dateToMillis(projectJson.getStartTime()));
    projectES.setEndTime(TimeUtil.dateToMillis(projectJson.getEndTime()));
    projectES.setCityDistricts(projectJson.getCityDistricts());
    projectES.setOwnerName(projectJson.getCustomer().getName());
    projectES.setContactName(projectJson.getContact().getName());
    projectES.setCustomerReference(projectJson.getCustomerReference());
    projectES.setAdditionalInfo(projectJson.getAdditionalInfo());
    projectES.setParentId(projectJson.getParentId());
    projectES.setIdentifier(projectJson.getIdentifier());
    return projectES;
  }
}
