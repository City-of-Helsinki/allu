package fi.hel.allu.ui.domain;

import java.time.ZonedDateTime;

/**
 * in Finnish: Hanke
 */
public class ProjectJson {
  private Integer id;
  private String name;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private String ownerName;
  private String contactName;
  private String email;
  private String phone;
  private String customerReference;
  private String additionalInfo;
  private Integer parentId;
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
   * in Finnish: hankkeen omistajan nimi. Yleensä yrityksen nimi.
   */
  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  /**
   * in Finnish: hankkeen kontaktin nimi. Yleensä ihmisen nimi.
   */
  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  /**
   * in Finnish: hankkeen sähköpostiosoite eli yleensä kontakti-ihmisen sähköpostiosoite.
   */
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * in Finnish: hankkeen puhelinnumero eli yleensä kontakti-ihmisen puhelinnumero.
   */
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
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
