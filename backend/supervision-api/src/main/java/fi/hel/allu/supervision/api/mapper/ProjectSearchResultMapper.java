package fi.hel.allu.supervision.api.mapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.supervision.api.domain.LocationSearchResult;
import fi.hel.allu.supervision.api.domain.ProjectSearchResult;

@Component
public class ProjectSearchResultMapper {

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;

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
    result.setLocations(getApplicationLocations(project.getId()));
    return result;
  }

  private List<LocationSearchResult> getApplicationLocations(Integer projectId) {
    ApplicationQueryParameters queryParameters = ApplicationSearchParameterMapper.queryParametersForProject(projectId);
    Page<ApplicationES> result = applicationServiceComposer.search(queryParameters, MapperUtil.DEFAULT_PAGE_REQUEST, Boolean.FALSE);
    return result.getContent().stream()
        .flatMap(a -> a.getLocations().stream())
        .map(l -> new LocationSearchResult(l.getAddress(), l.getCityDistrictId(), l.getGeometry()))
        .collect(Collectors.toList());
  }
}
