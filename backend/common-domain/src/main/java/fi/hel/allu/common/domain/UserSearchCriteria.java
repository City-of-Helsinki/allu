package fi.hel.allu.common.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;

public class UserSearchCriteria {
  private RoleType roleType;
  private ApplicationType applicationType;
  private Integer cityDistrictId;

  public RoleType getRoleType() {
    return roleType;
  }

  public void setRoleType(RoleType roleType) {
    this.roleType = roleType;
  }

  public ApplicationType getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(ApplicationType applicationType) {
    this.applicationType = applicationType;
  }

  public Integer getCityDistrictId() {
    return cityDistrictId;
  }

  public void setCityDistrictId(Integer cityDistrictId) {
    this.cityDistrictId = cityDistrictId;
  }
}
