package fi.hel.allu.servicecore.domain.informationrequest;

import fi.hel.allu.common.domain.types.InformationRequestStatus;
import fi.hel.allu.model.domain.InformationRequestField;
import fi.hel.allu.servicecore.domain.InformationRequestFieldJson;

import java.time.ZonedDateTime;
import java.util.List;

public class InformationRequestSummaryJson {
  private Integer informationRequestId;
  private Integer applicationId;
  private InformationRequestStatus status;
  private ZonedDateTime creationTime;
  private ZonedDateTime responseReceived;
  private String creator;
  private String respondent;
  private boolean updateWithoutRequest;
  private List<InformationRequestFieldJson> requestedFields;
  private List<InformationRequestFieldJson> responseFields;

  public InformationRequestSummaryJson() {
  }

  public Integer getInformationRequestId() {
    return informationRequestId;
  }

  public void setInformationRequestId(Integer informationRequestId) {
    this.informationRequestId = informationRequestId;
  }

  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  public InformationRequestStatus getStatus() {
    return status;
  }

  public void setStatus(InformationRequestStatus status) {
    this.status = status;
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

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getRespondent() {
    return respondent;
  }

  public void setRespondent(String respondent) {
    this.respondent = respondent;
  }

  public boolean isUpdateWithoutRequest() {
    return updateWithoutRequest;
  }

  public void setUpdateWithoutRequest(boolean updateWithoutRequest) {
    this.updateWithoutRequest = updateWithoutRequest;
  }

  public List<InformationRequestFieldJson> getRequestedFields() {
    return requestedFields;
  }

  public void setRequestedFields(List<InformationRequestFieldJson> requestedFields) {
    this.requestedFields = requestedFields;
  }

  public List<InformationRequestFieldJson> getResponseFields() {
    return responseFields;
  }

  public void setResponseFields(List<InformationRequestFieldJson> responseFields) {
    this.responseFields = responseFields;
  }
}
