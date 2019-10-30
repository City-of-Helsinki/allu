package fi.hel.allu.supervision.api.mapper;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.CustomerES;
import fi.hel.allu.supervision.api.domain.ApplicationSearchResult;
import fi.hel.allu.supervision.api.domain.LocationSearchResult;

@Component
public class ApplicationSearchResultMapper {

  public ApplicationSearchResult mapToSearchResult(ApplicationES applicationES) {
    ApplicationSearchResult application = new ApplicationSearchResult();
    CustomerES applicant = applicationES.getCustomers().getApplicant() != null ? applicationES.getCustomers().getApplicant().getCustomer() : null;
    if (applicant != null) {
      application.setApplicantId(applicant.getId());
      application.setApplicantName(applicant.getName());
    }
    application.setApplicationId(applicationES.getApplicationId());
    application.setApplicationTags(applicationES.getApplicationTags().stream().map(ApplicationTagType::valueOf).collect(Collectors.toList()));
    application.setId(applicationES.getId());
    application.setLocations(applicationES.getLocations()
        .stream()
        .map(l -> new LocationSearchResult(l.getAddress(), l.getAdditionalInfo(), l.getCityDistrictId(), MapperUtil.toGeometry(l.getGeometry())))
        .collect(Collectors.toList()));
    Optional.ofNullable(applicationES.getOwner()).ifPresent(owner -> {
      application.setOwnerRealName(owner.getRealName());
      application.setOwnerUserName(owner.getRealName());
    });
    Optional.ofNullable(applicationES.getProject()).ifPresent(project -> {
      application.setProjectId(project.getId());
      application.setProjectIdentifier(project.getIdentifier());
    });
    application.setStatus(applicationES.getStatus().getValue());
    application.setType(applicationES.getType().getValue());
    application.setStartTime(TimeUtil.millisToZonedDateTime(applicationES.getStartTime()));
    application.setEndTime(TimeUtil.millisToZonedDateTime(applicationES.getEndTime()));
    return application;
  }

}
