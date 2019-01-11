package fi.hel.allu.servicecore.domain;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.common.validator.NotFalse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Items stored in distribution list. Each item describes a single distribution target, which will receive application decision.
 */
@ApiModel(value = "Distribution entry describing a single distribution target, which will receive application decision")
@NotFalse(rules = {"email, hasRecipientValidation, Either email or postal address must have values"})
public class DistributionEntryJson {
  @NotNull(message = "{distributionentry.type}")
  private DistributionType distributionType;
  private String name;
  private String email;
  private PostalAddressJson postalAddress;

  @ApiModelProperty(value = "The media type used to distribute the decision.")
  public DistributionType getDistributionType() {
    return distributionType;
  }

  public void setDistributionType(DistributionType distributionType) {
    this.distributionType = distributionType;
  }

  @ApiModelProperty(value = "Name of the distribution recipient")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(value = "Email address of the distribution recipient")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @ApiModelProperty(value = "Postal address of the distribution recipient")
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
