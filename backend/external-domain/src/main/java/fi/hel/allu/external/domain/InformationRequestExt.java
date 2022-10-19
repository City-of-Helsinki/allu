package fi.hel.allu.external.domain;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Information request data.")
public class InformationRequestExt {

  private Integer id;
  private Integer applicationId;
  private List<InformationRequestFieldExt> fields;

  public InformationRequestExt() {
  }

  public InformationRequestExt(Integer id, Integer applicationId,
      List<InformationRequestFieldExt> fields) {
    this.id = id;
    this.applicationId = applicationId;
    this.fields = fields;
  }

  @Schema(description = "Id of the information request.")
  public Integer getInformationRequestId() {
    return id;
  }

  public void setInformationRequestId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Id of the application information is requested for.")
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  @Schema(description = "Requested fields")
  public List<InformationRequestFieldExt> getFields() {
    return fields;
  }

  public void setFields(List<InformationRequestFieldExt> fields) {
    this.fields = fields;
  }
}
