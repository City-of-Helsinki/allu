package fi.hel.allu.external.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.validator.NotFalse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@NotFalse(rules = {
    "customerWithContacts, hasOneOrderer, {cablereport.orderer}"
})
@ApiModel("Cable report (johtoselvitys) input model.")
public class CableReportExt extends BaseApplicationExt {

  @NotEmpty(message = "{application.clientApplicationKind}")
  private String clientApplicationKind;

  @NotEmpty(message = "{application.workDescription}")
  private String workDescription;

  @Valid
  private CustomerWithContactsExt propertyDeveloperWithContacts;

  @NotNull(message = "{cablereport.contractor}")
  @Valid
  private CustomerWithContactsExt contractorWithContacts;

  @ApiModelProperty(value = "Application kind of the client system. Allu application kind will be selected by handler according to this value", required = true)
  public String getClientApplicationKind() {
    return clientApplicationKind;
  }

  public void setClientApplicationKind(String clientApplicationKind) {
    this.clientApplicationKind = clientApplicationKind;
  }

  @ApiModelProperty(value = "Work description", required = true)
  public String getWorkDescription() {
    return workDescription;
  }

  public void setWorkDescription(String workDescription) {
    this.workDescription = workDescription;
  }

  @ApiModelProperty(value = "Property developer (rakennuttaja)")
  public CustomerWithContactsExt getPropertyDeveloperWithContacts() {
    return propertyDeveloperWithContacts;
  }

  public void setPropertyDeveloperWithContacts(CustomerWithContactsExt propertyDeveloperWithContacts) {
    this.propertyDeveloperWithContacts = propertyDeveloperWithContacts;
  }

  @ApiModelProperty(value = "Contractor (työn suorittaja)", required = true)
  public CustomerWithContactsExt getContractorWithContacts() {
    return contractorWithContacts;
  }

  public void setContractorWithContacts(CustomerWithContactsExt contractorWithContacts) {
    this.contractorWithContacts = contractorWithContacts;
  }

  @JsonIgnore
  public boolean getHasOneOrderer() {
    return ordererCount(contractorWithContacts) + ordererCount(propertyDeveloperWithContacts) +
        ordererCount(getRepresentativeWithContacts()) + ordererCount(getCustomerWithContacts()) == 1;
  }

  private long ordererCount(CustomerWithContactsExt customer) {
    return customer != null ? customer.getContacts().stream().filter(c -> BooleanUtils.isTrue(c.getOrderer())).count() : 0;
  }

}
