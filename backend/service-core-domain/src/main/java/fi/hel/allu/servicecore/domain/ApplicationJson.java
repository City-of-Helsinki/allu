package fi.hel.allu.servicecore.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * in Finnish: Hakemus
 */
@ApiModel(value = "Application")
public class ApplicationJson extends BaseApplicationJson {

  @NotEmpty(message = "{application.customersWithContacts}")
  private List<CustomerWithContactsJson> customersWithContacts;

  public <U extends CreateApplicationJson> ApplicationJson(U application) {
    super(application);
  }

  public ApplicationJson() {
  }

  @ApiModelProperty(value = "Application customers with their contacts", readOnly = true)
  public List<CustomerWithContactsJson> getCustomersWithContacts() {
    return customersWithContacts;
  }

  public void setCustomersWithContacts(List<CustomerWithContactsJson> customersWithContacts) {
    this.customersWithContacts = customersWithContacts;
  }
}
