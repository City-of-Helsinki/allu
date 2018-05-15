package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;
import java.util.List;

public class InformationRequest {
  private Integer id;
  private Integer applicationId;
  private ZonedDateTime creationTime;
  private int creatorId;
  private boolean open;
  private List<InformationRequestField> fields;

  public InformationRequest() {
  }

  public InformationRequest(Integer id, Integer applicationId, boolean open, List<InformationRequestField> fields) {
    this.id = id;
    this.applicationId = applicationId;
    this.open = open;
    this.fields = fields;
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

  public boolean isOpen() {
    return open;
  }

  public void setOpen(boolean open) {
    this.open = open;
  }
}
