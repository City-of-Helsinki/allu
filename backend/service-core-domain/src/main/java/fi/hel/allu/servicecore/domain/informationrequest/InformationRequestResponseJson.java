package fi.hel.allu.servicecore.domain.informationrequest;

import java.util.List;

import fi.hel.allu.common.domain.types.InformationRequestFieldKey;
import fi.hel.allu.servicecore.domain.ApplicationJson;

public class InformationRequestResponseJson {

  private Integer informationRequestId;
  private Integer applicationId;
  private ApplicationJson responseData;
  private List<InformationRequestFieldKey> updatedFields;

  public InformationRequestResponseJson() {
  }

  public InformationRequestResponseJson(Integer informationRequestId, Integer applicationId,
      ApplicationJson responseData, List<InformationRequestFieldKey> updatedFields) {
    this.informationRequestId = informationRequestId;
    this.applicationId = applicationId;
    this.responseData = responseData;
    this.updatedFields = updatedFields;
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

  public ApplicationJson getResponseData() {
    return responseData;
  }

  public void setResponseData(ApplicationJson responseData) {
    this.responseData = responseData;
  }

  public List<InformationRequestFieldKey> getUpdatedFields() {
    return updatedFields;
  }

  public void setUpdatedFields(List<InformationRequestFieldKey> updatedFields) {
    this.updatedFields = updatedFields;
  }
}
