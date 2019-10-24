package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;
import java.util.List;

import fi.hel.allu.common.domain.types.InformationRequestStatus;

public class InformationRequest {
  private Integer id;
  private Integer applicationId;
  private ZonedDateTime creationTime;
  private ZonedDateTime responseReceived;
  private int creatorId;
  private InformationRequestStatus status;
  private List<InformationRequestField> fields;

  public InformationRequest() {
  }

  public InformationRequest(Integer id, Integer applicationId, InformationRequestStatus status, List<InformationRequestField> fields) {
    this.id = id;
    this.applicationId = applicationId;
    this.fields = fields;
    this.status = status;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  public ZonedDateTime getResponseReceived() {
    return responseReceived;
  }

  public void setResponseReceived(ZonedDateTime responseReceived) {
    this.responseReceived = responseReceived;
  }

  public int getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(int creatorId) {
    this.creatorId = creatorId;
  }

  public List<InformationRequestField> getFields() {
    return fields;
  }

  public void setFields(List<InformationRequestField> fields) {
    this.fields = fields;
  }

  public InformationRequestStatus getStatus() {
    return status;
  }

  public void setStatus(InformationRequestStatus status) {
    this.status = status;
  }
}
