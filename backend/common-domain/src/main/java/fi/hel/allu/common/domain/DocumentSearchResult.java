package fi.hel.allu.common.domain;

import java.time.ZonedDateTime;

public class DocumentSearchResult {

  private Integer id;
  private ZonedDateTime documentDate;
  private Integer decisionMakerId;

  public DocumentSearchResult() {
  }

  public DocumentSearchResult(Integer id, ZonedDateTime documentDate, Integer decisionMakerId) {
    this.id = id;
    this.documentDate = documentDate;
    this.decisionMakerId = decisionMakerId;
  }

  public DocumentSearchResult(Integer id, ZonedDateTime documentDate) {
    this.id = id;
    this.documentDate = documentDate;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public ZonedDateTime getDocumentDate() {
    return documentDate;
  }

  public void setDocumentDate(ZonedDateTime documentDate) {
    this.documentDate = documentDate;
  }

  public Integer getDecisionMakerId() {
    return decisionMakerId;
  }

  public void setDecisionMakerId(Integer decisionMakerId) {
    this.decisionMakerId = decisionMakerId;
  }

}
