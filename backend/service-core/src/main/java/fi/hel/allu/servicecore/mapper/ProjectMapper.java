package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.IdInterface;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.search.domain.ProjectES;
import fi.hel.allu.servicecore.domain.ContactJson;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.domain.ModifyProjectJson;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.servicecore.service.ContactService;
import fi.hel.allu.servicecore.service.CustomerService;
import fi.hel.allu.servicecore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Mapper for projects in their different forms.
 */
@Component
public class ProjectMapper {

  private final CustomerService customerService;
  private final ContactService contactService;
  private final UserService userService;

  @Autowired
  public ProjectMapper(@Lazy CustomerService customerService, @Lazy ContactService contactService, UserService userService) {
    this.customerService = customerService;
    this.contactService = contactService;
    this.userService = userService;
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
    if (projectJson.getCreator() != null) {
      projectDomain.setCreatorId(projectJson.getCreator().getId());
    }
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
    if (projectDomain.getCreatorId() != null) {
      projectJson.setCreator(userService.findUserById(projectDomain.getCreatorId()));
    }
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
    if (projectJson.getCreator() != null) {
      projectES.setCreator(projectJson.getCreator().getRealName());
    }
    return projectES;
  }

  public ProjectJson mapCreateJsonToProjectJson(ModifyProjectJson createJson) {
    ProjectJson projectJson = new ProjectJson();
    setProjectFields(createJson, projectJson);
    return projectJson;
  }

  public void setProjectFields(ModifyProjectJson modifyJson, ProjectJson projectJson) {
    projectJson.setCustomer(new CustomerJson(modifyJson.getCustomerId()));
    projectJson.setContact(new ContactJson(modifyJson.getContactId()));
    projectJson.setIdentifier(modifyJson.getIdentifier());
    projectJson.setAdditionalInfo(modifyJson.getAdditionalInfo());
    projectJson.setCustomerReference(modifyJson.getCustomerReference());
    projectJson.setName(modifyJson.getName());
  }

  public <T extends IdInterface, U> List<U> populateValues(Map<Integer, List<U>> mappedApplications,
                                                           BiConsumer<T, U> setValue, List<T> listToPopulate) {
    List<U> result = new ArrayList<>();
    for (T jsonValue : listToPopulate) {
      List<U> applicationJsonList = mappedApplications.get(jsonValue.getId());
      applicationJsonList.forEach(applicationJson -> setValue.accept(jsonValue, applicationJson));
      result.addAll(applicationJsonList);
    }
    return result;
  }
}