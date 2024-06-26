package fi.hel.allu.servicecore.domain;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.common.validator.NotFalse;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Items stored in distribution list. Each item describes a single distribution target, which will receive application decision.
 */
@Schema(description = "Distribution entry describing a single distribution target, which will receive application decision")
@NotFalse(rules = {"email, hasRecipientValidation, Either email or postal address must have values"})
public class DistributionEntryJson {
  private Integer id;
  @NotNull(message = "{distributionentry.type}")
  private DistributionType distributionType;
  private String name;
  private String email;
  private PostalAddressJson postalAddress;

  @Schema(description = "Id of the distribution entry")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "The media type used to distribute the decision.")
  public DistributionType getDistributionType() {
    return distributionType;
  }

  public void setDistributionType(DistributionType distributionType) {
    this.distributionType = distributionType;
  }

  @Schema(description = "Name of the distribution recipient")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Schema(description = "Email address of the distribution recipient")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Schema(description = "Postal address of the distribution recipient")
  public PostalAddressJson getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressJson postalAddress) {
    this.postalAddress = postalAddress;
  }

  @JsonIgnore
  public boolean getHasRecipientValidation() {
    if (DistributionType.EMAIL.equals(distributionType)) {
      return email != null;
    } else {
      return postalAddress != null;
    }
  }
}
