package fi.hel.allu.external.domain;

import io.swagger.annotations.ApiModelProperty;

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

  @ApiModelProperty(value = "Application ID")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Application identifier (hakemustunnus)")
  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  @ApiModelProperty(value = "Application address / fixed location name")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
