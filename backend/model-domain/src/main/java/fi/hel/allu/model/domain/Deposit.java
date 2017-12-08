package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;
import fi.hel.allu.common.domain.types.DepositStatusType;

/**
 * Deposit of application
 *
 */
public class Deposit {
  private Integer id;
  @NotNull
  private Integer applicationId;
  @NotNull
  private Integer amount;
  private String reason;
  @NotNull
  private DepositStatusType status;

  private Integer creatorId;
  private ZonedDateTime creationTime;

  public Deposit(Integer id, Integer applicationId, Integer amount, String reason, DepositStatusType status,
                 ZonedDateTime creationTime, Integer creatorId) {
    this.id = id;
    this.applicationId = applicationId;
    this.amount = amount;
    this.reason = reason;
    this.status = status;
    this.creationTime = creationTime;
    this.creatorId = creatorId;
  }

  public Deposit() {
  }

  /**
   * Database id of the deposit
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Database ID of the application of the deposit.
   */
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  /**
   * Deposit amount in cents
   */
  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  /**
   * Reason for deposit
   */
  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  /**
   * Current status of deposit
   */
  public DepositStatusType getStatus() {
    return status;
  }

  public void setStatus(DepositStatusType status) {
    this.status = status;
  }

  /**
   * Creator of the deposit
   */
  public Integer getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Integer creatorId) {
    this.creatorId = creatorId;
  }

  /**
   * Creation time of the deposit
   */
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

}
