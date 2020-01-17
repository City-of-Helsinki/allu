package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Placement contract signing information")
public class ContractSigningInfoExt {

  @NotBlank(message = "{contract.signer}")
  private String signer;
  @NotNull(message = "{contract.signingTime}")
  private ZonedDateTime signingTime;


  @ApiModelProperty(value = "Name of the contract signer", required = true)
  public String getSigner() {
    return signer;
  }

  public void setSigner(String signer) {
    this.signer = signer;
  }

  @ApiModelProperty(value = "Contract signing time", required = true)
  public ZonedDateTime getSigningTime() {
    return signingTime;
  }

  public void setSigningTime(ZonedDateTime signingTime) {
    this.signingTime = signingTime;
  }

}
