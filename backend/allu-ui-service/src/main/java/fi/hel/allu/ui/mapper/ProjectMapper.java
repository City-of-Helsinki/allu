package fi.hel.allu.ui.mapper;

import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.search.domain.ProjectES;
import fi.hel.allu.ui.domain.ProjectJson;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Mapper for projects in their different forms.
 */
@Component
public class ProjectMapper {
  public Project createProjectModel(ProjectJson projectJson) {
    Project projectDomain = new Project();
    projectDomain.setId(projectJson.getId());
    projectDomain.setName(projectJson.getName());
    projectDomain.setStartTime(projectJson.getStartTime());
    projectDomain.setEndTime(projectJson.getEndTime());
    if (projectJson.getCityDistricts() != null) {
      projectDomain.setCityDistricts(projectJson.getCityDistricts().toArray(new Integer[0]));
    }
    projectDomain.setOwnerName(projectJson.getOwnerName());
    projectDomain.setContactName(projectJson.getContactName());
    projectDomain.setEmail(projectJson.getEmail());
    projectDomain.setPhone(projectJson.getPhone());
    projectDomain.setCustomerReference(projectJson.getCustomerReference());
    projectDomain.setAdditionalInfo(projectJson.getAdditionalInfo());
    projectDomain.setParentId(projectJson.getParentId());
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
    projectJson.setOwnerName(projectDomain.getOwnerName());
    projectJson.setContactName(projectDomain.getContactName());
    projectJson.setEmail(projectDomain.getEmail());
    projectJson.setPhone(projectDomain.getPhone());
    projectJson.setCustomerReference(projectDomain.getCustomerReference());
    projectJson.setAdditionalInfo(projectDomain.getAdditionalInfo());
    projectJson.setParentId(projectDomain.getParentId());
    return projectJson;
  }

  public ProjectES createProjectESModel(ProjectJson projectJson) {
    ProjectES projectES = new ProjectES();
    projectES.setId(projectJson.getId());
    projectES.setName(projectJson.getName());
    projectES.setStartTime(TimeUtil.dateToMillis(projectJson.getStartTime()));
    projectES.setEndTime(TimeUtil.dateToMillis(projectJson.getEndTime()));
    projectES.setCityDistricts(projectJson.getCityDistricts());
    projectES.setOwnerName(projectJson.getOwnerName());
    projectES.setContactName(projectJson.getContactName());
    projectES.setEmail(projectJson.getEmail());
    projectES.setPhone(projectJson.getPhone());
    projectES.setCustomerReference(projectJson.getCustomerReference());
    projectES.setAdditionalInfo(projectJson.getAdditionalInfo());
    projectES.setParentId(projectJson.getParentId());

    return projectES;
  }
}
