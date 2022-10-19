package fi.hel.allu.servicecore.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Application customer id and related contacts' ids")
public class CreateCustomerWithContactsJson {

  @NotNull
  private Integer customerId;
  private List<Integer> contactIds = new ArrayList<>();

  @Schema(description = "Customer id")
  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  @Schema(description = "Contact ids of the customer for the application")
  public List<Integer> getContactIds() {
    return contactIds;
  }

  public void setContactIds(List<Integer> contacts) {
    this.contactIds = contacts;
  }
}
