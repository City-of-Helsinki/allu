package fi.hel.allu.search.domain;

import fi.hel.allu.common.types.CustomerType;

/**
 * ElasticSearch mapping for customer.
 */
public class CustomerES {
  private Integer id;
  private String name;
  private String registryKey;
  private CustomerType type;
  private boolean isActive;

  public CustomerES() {
    // JSON serialization
  }

  public CustomerES(Integer id, String name, String registryKey, CustomerType type, boolean isActive) {
    this.id = id;
    this.name = name;
    this.registryKey = registryKey;
    this.type = type;
    this.isActive = isActive;
  }

  /**
   * @return  ElasticSearch id of the customer. The same as database id.
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * @return  Name of the customer.
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Registry key i.e. business id (Y-tunnus) or social security id (henkilötunnus) of the customer.
   *
   * @return Registry key.
   */
  public String getRegistryKey() {
    return registryKey;
  }

  public void setRegistryKey(String registryKey) {
    this.registryKey = registryKey;
  }

  /**
   * Type of the customer (on of person, company, association, property, other)
   */
  public CustomerType getType() {
    return type;
  }

  public void setType(CustomerType type) {
    this.type = type;
  }

  /*
   * @return  True, if the user is active i.e. has not been marked as deleted.
   */
  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }
}
