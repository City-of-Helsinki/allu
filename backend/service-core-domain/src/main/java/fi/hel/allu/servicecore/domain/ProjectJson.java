package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * in Finnish: Hanke
 */
public class ProjectJson {
  private Integer id;
  private String name;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private List<Integer> cityDistricts;
  private String customerReference;
  private String additionalInfo;
  private Integer parentId;
  @NotNull
  private CustomerJson customer;
  @NotNull
  private ContactJson contact;
  @NotNull
  private String identifier;

  /**
   * in Finnish: Hankkeen tunniste
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * in Finnish: Hankkeen nimi
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * in Finnish: hankkeen alkuaika. Lasketaan hankkeeseen sisältyvien hakemusten alkuajoista.
   */
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  /**
   * in Finnish: hankkeen loppumisaika. Lasketaan hankkeeseen sisältyvien hakemusten loppumisajoista.
   */
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  /**
   * in Finnish: hankkeen kaupunginosat. Lasketaan hankkeeseen sisältyvien hakemusten kaupunginosista.
   */
  public List<Integer> getCityDistricts() {
    return cityDistricts;
  }

  public void setCityDistricts(List<Integer> cityDistricts) {
    this.cityDistricts = cityDistricts;
  }

  /**
   * in Finnish: asiakkaan viite tai työnumero.
   */
  public String getCustomerReference() {
    return customerReference;
  }

  public void setCustomerReference(String customerReference) {
    this.customerReference = customerReference;
  }

  /**
   * in Finnish: Hankkeen lisätietoa
   */
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  /**
   * in Finnish: hankkeen äitihanke eli hanke, joka tämän hankkeen sisältää, mikäli äitihanke on olemassa.
   *
   * @return Id of the parent of <code>null</code> if parent does not exist.
   */
  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  public CustomerJson getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerJson customer) {
    this.customer = customer;
  }

  public ContactJson getContact() {
    return contact;
  }

  public void setContact(ContactJson contact) {
    this.contact = contact;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ProjectJson that = (ProjectJson) o;

    return id != null ? id.equals(that.id) : that.id == null;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
