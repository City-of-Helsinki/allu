package fi.hel.allu.supervision.api.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.supervision.api.domain.ProjectSearchResult;

@Component
public class ProjectSearchResultMapper {

  public ProjectSearchResult mapToSearchResult(ProjectJson project) {
    ProjectSearchResult result = new ProjectSearchResult();
    Optional.ofNullable(project.getContact()).ifPresent(c -> result.setContactName(c.getName()));
    result.setEndTime(project.getEndTime());
    result.setId(project.getId());
    result.setIdentifier(project.getIdentifier());
    result.setName(project.getName());
    Optional.ofNullable(project.getCustomer()).ifPresent(o -> result.setOwnerName(o.getName()));
    result.setStartTime(project.getStartTime());
    result.setCustomerReference(project.getCustomerReference());
    return result;

  }
}
