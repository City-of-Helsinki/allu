package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public abstract class CreateApplicationJson extends BaseApplicationJson {

  private Integer projectId;
  private Integer ownerId;
  private Integer handlerId;

  @NotNull
  @Valid
  private CreateCustomerWithContactsJson customerApplicantWithContacts;

  @ApiModelProperty(value = "Project this application belongs to")
  public Integer getProjectId() {
    return projectId;
  }

  @UpdatableProperty
  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  @ApiModelProperty(value = "Owner of the application")
  public Integer getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Integer ownerId) {
    this.ownerId = ownerId;
  }

  @ApiModelProperty(value = "Handler of the application")
  public Integer getHandlerId() {
    return handlerId;
  }

  public void setHandlerId(Integer handlerId) {
    this.handlerId = handlerId;
  }

  public CreateCustomerWithContactsJson getCustomerApplicantWithContacts() {
    return customerApplicantWithContacts;
  }

  public void setCustomerApplicantWithContacts(CreateCustomerWithContactsJson customerApplicantWithContacts) {
    this.customerApplicantWithContacts = customerApplicantWithContacts;
  }

  public Map<CustomerRoleType, CreateCustomerWithContactsJson> getAllCustomersWithContactsByCustomerRoleType() {
    Map<CustomerRoleType, CreateCustomerWithContactsJson> customersWithContacts = new HashMap<>();
    if (customerApplicantWithContacts != null) {
      customersWithContacts.put(CustomerRoleType.APPLICANT, customerApplicantWithContacts);
    }
    return customersWithContacts;
  }
}
