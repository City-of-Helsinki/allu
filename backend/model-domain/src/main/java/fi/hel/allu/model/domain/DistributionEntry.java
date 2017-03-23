package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.DistributionType;

/**
 * Items stored in distribution list. Each item describes a single distribution target, which will receive application decision.
 */
public class DistributionEntry implements PostalAddressItem {
  private Integer id;
  private Integer applicationId;
  private DistributionType distributionType;
  private String name;
  private String email;
  private PostalAddress postalAddress;

  /**
   * Database id.
   *
   * @return  Database id.
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * The application owning this entry.
   *
   * @return  The application owning this entry.
   */
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  /**
   * The media type used to distribute the decision.
   *
   * @return  The media type used to distribute the decision.
   */
  public DistributionType getDistributionType() {
    return distributionType;
  }

  public void setDistributionType(DistributionType distributionType) {
    this.distributionType = distributionType;
  }

  /**
   * Name of the distribution recipient.
   *
   * @return  Name of the distribution recipient.
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * The email address of the distribution recipient.
   *
   * @return  The email address of the distribution recipient.
   */
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public PostalAddress getPostalAddress() {
    return postalAddress;
  }

  @Override
  public void setPostalAddress(PostalAddress postalAddress) {
    this.postalAddress = postalAddress;
  }
}
