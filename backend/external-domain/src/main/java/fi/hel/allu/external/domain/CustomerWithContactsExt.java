package fi.hel.allu.external.domain;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Application customer and related contacts")
public class CustomerWithContactsExt {
  @NotNull(message = "{customerWithContacts.customer}")
  @Valid
  private CustomerExt customer;
  @NotEmpty(message = "{customerWithContacts.contact}")
  @Valid
  private List<ContactExt> contacts = new ArrayList<>();

  @Schema(description = "Application customer", required = true)
  public CustomerExt getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerExt customer) {
    this.customer = customer;
  }

  @Schema(description = "Application customer contacts", required = true)
  public List<ContactExt> getContacts() {
    return contacts;
  }

  public void setContacts(List<ContactExt> contacts) {
    this.contacts = contacts;
  }

}
