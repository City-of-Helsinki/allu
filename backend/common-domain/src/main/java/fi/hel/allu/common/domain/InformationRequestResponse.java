package fi.hel.allu.common.domain;

import java.util.List;

import fi.hel.allu.common.domain.types.InformationRequestFieldKey;

public class InformationRequestResponse {

  private Integer informationRequestId;
  // List containing fields updated for information request
  private List<InformationRequestFieldKey> responseFields;
  // External application containing updated application data.
  private ExternalApplication application;

  public InformationRequestResponse() {
  }

  public InformationRequestResponse(List<InformationRequestFieldKey> responseFields, ExternalApplication application) {
    this.responseFields = responseFields;
    this.application = application;
  }

  public List<InformationRequestFieldKey> getResponseFields() {
    return responseFields;
  }

  public void setResponseFields(List<InformationRequestFieldKey> responseFields) {
    this.responseFields = responseFields;
  }

  public ExternalApplication getApplication() {
    return application;
  }

  public void setApplication(ExternalApplication application) {
    this.application = application;
  }

  public Integer getInformationRequestId() {
    return informationRequestId;
  }

  public void setInformationRequestId(Integer informationRequestId) {
    this.informationRequestId = informationRequestId;
  }


}
