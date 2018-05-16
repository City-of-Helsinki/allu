package fi.hel.allu.servicecore.domain;

import java.util.List;

import fi.hel.allu.common.domain.types.InformationRequestStatus;

/**
 * Information request data.
 *
 */
public class InformationRequestJson {

  private Integer id;
  private Integer applicationId;
  private List<InformationRequestFieldJson> fields;
  private InformationRequestStatus status;

  public InformationRequestJson() {
  }

  public InformationRequestJson(Integer id, Integer applicationId, List<InformationRequestFieldJson> fields,
      InformationRequestStatus status) {
    this.id = id;
    this.applicationId = applicationId;
    this.fields = fields;
    this.status = status;
  }

  public Integer getId() {
    return id;
  }

  public void setInformationRequestId(Integer id) {
    this.id = id;
  }

  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  public List<InformationRequestFieldJson> getFields() {
    return fields;
  }

  public void setFields(List<InformationRequestFieldJson> fields) {
    this.fields = fields;
  }

  public InformationRequestStatus getStatus() {
    return status;
  }

  public void setStatus(InformationRequestStatus status) {
    this.status = status;
  }

}
