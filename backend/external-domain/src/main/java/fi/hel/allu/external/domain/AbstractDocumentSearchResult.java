package fi.hel.allu.external.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Base class for decision / approval document search results.
 */
public abstract class AbstractDocumentSearchResult {
  private Integer id;
  private String applicationId;
  private String address;


  public AbstractDocumentSearchResult() {
  }

  public AbstractDocumentSearchResult(Integer id, String applicationId, String address) {
    this.id = id;
    this.applicationId = applicationId;
    this.address = address;
  }

  @Schema(description = "Application ID")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Application identifier (hakemustunnus)")
  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  @Schema(description = "Application address / fixed location name")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
