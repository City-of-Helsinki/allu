package fi.hel.allu.external.domain;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Application customer and related contacts")
public class CustomerWithContactsExt {
  @NotNull(message = "{customerWithContacts.customer}")
  @Valid
  private CustomerExt customer;
  @NotEmpty(message = "{customerWithContacts.contact}")
  @Valid
  private List<ContactExt> contacts = new ArrayList<>();

  @ApiModelProperty(value = "Application customer", required = true)
  public CustomerExt getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerExt customer) {
    this.customer = customer;
  }

  @ApiModelProperty(value = "Application customer contacts", required = true)
  public List<ContactExt> getContacts() {
    return contacts;
  }

  public void setContacts(List<ContactExt> contacts) {
    this.contacts = contacts;
  }

}
