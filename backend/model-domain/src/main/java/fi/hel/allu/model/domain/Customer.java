package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.CustomerType;

/**
 * in Finnish: Toimeksiantaja
 */
public class Customer {

  private Integer id;
  private CustomerType type;
  private String sapId;
  private Integer personId;
  private Integer organizationId;

  /**
   * in Finnish: Toimeksiantajan tunniste
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * in Finnish: tyyppi, ihminen tai yritys/yhteisö
   */
  public CustomerType getType() {
    return type;
  }

  public void setType(CustomerType type) {
    this.type = type;
  }

  /**
   * in Finnish: SAP-tunnus
   */
  public String getSapId() {
    return sapId;
  }

  public void setSapId(String sapId) {
    this.sapId = sapId;
  }

  /**
   * in Finnish: Toimeksiantajaan liittyvän henkilön tunniste
   */
  public Integer getPersonId() {
    return personId;
  }

  public void setPersonId(Integer personId) {
    this.personId = personId;
  }

  /**
   * in Finnish: Toimeksiantajaan liittyvän organisaation tunniste
   */
  public Integer getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(Integer organizationId) {
    this.organizationId = organizationId;
  }
}
