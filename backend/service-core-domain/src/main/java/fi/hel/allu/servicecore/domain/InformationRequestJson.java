package fi.hel.allu.servicecore.domain;

import java.util.List;

/**
 * Information request data.
 *
 */
public class InformationRequestJson {

  private Integer id;
  private Integer applicationId;
  private List<InformationRequestFieldJson> fields;
  private boolean open;

  public InformationRequestJson() {
  }

  public InformationRequestJson(Integer id, Integer applicationId,
      List<InformationRequestFieldJson> fields, boolean open) {
    this.id = id;
    this.applicationId = applicationId;
    this.fields = fields;
    this.open = open;
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

  public boolean isOpen() {
    return open;
  }

  public void setOpen(boolean open) {
    this.open = open;
  }

}
