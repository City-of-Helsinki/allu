package fi.hel.allu.model.service.changehistory;

import fi.hel.allu.model.domain.Contact;

public class ContactChange {
  private final String contactName;
  private final Integer id;
  public ContactChange(Contact contact) {
    if (contact != null) {
      this.contactName = contact.getName();
      this.id = contact.getId();
    } else {
      this.contactName = null;
      this.id = null;
    }
  }

  public String getContactName() {
    return contactName;
  }

  public Integer getId() {
    return id;
  }
}
