package fi.hel.allu.servicecore.domain;
import io.swagger.v3.oas.annotations.media.Schema;

public abstract class BaseProjectJson {
  private String name;
  private String customerReference;
  private String identifier;
  private String additionalInfo;

  @Schema(description = "Name of the project")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Schema(description = "Customer reference (asiakkaan viite tai työnumero) ")
  public String getCustomerReference() {
    return customerReference;
  }

  public void setCustomerReference(String customerReference) {
    this.customerReference = customerReference;
  }

  @Schema(description = "Project identifier (hanketunniste)")
  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  @Schema(description = "Additional information")
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

}
