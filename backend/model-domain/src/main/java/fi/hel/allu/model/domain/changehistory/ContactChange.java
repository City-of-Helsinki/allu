package fi.hel.allu.model.domain.changehistory;

import fi.hel.allu.model.domain.Contact;

public class ContactChange {
  private final String name;
  private final Integer id;
  public ContactChange(Contact contact) {
    if (contact != null) {
      this.name = contact.getName();
      this.id = contact.getId();
    } else {
      this.name = null;
      this.id = null;
    }
  }

  public String getName() {
    return name;
  }

  public Integer getId() {
    return id;
  }
}
