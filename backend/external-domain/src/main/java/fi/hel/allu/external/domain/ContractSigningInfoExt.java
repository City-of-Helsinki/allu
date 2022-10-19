package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Placement contract signing information")
public class ContractSigningInfoExt {

  @NotBlank(message = "{contract.signer}")
  private String signer;
  @NotNull(message = "{contract.signingTime}")
  private ZonedDateTime signingTime;


  @Schema(description = "Name of the contract signer", required = true)
  public String getSigner() {
    return signer;
  }

  public void setSigner(String signer) {
    this.signer = signer;
  }

  @Schema(description = "Contract signing time", required = true)
  public ZonedDateTime getSigningTime() {
    return signingTime;
  }

  public void setSigningTime(ZonedDateTime signingTime) {
    this.signingTime = signingTime;
  }

}
