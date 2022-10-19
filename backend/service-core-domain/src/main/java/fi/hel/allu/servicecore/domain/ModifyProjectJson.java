package fi.hel.allu.servicecore.domain;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Project creation and modification model")
public class ModifyProjectJson extends BaseProjectJson {

  @NotNull(message = "{customerWithContacts.customer}")
  private Integer customerId;
  @NotNull(message = "{customerWithContacts.contact}")
  private Integer contactId;

  public ModifyProjectJson() {
    super();
  }

  @Schema(description = "Project customer ID")
  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  @Schema(description = "Project contact ID")
  public Integer getContactId() {
    return contactId;
  }

  public void setContactId(Integer contactId) {
    this.contactId = contactId;
  }

}
