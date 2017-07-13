package fi.hel.allu.model.domain;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.List;

/**
 * REST payload for contact change requests (create and update)
 */
public class ContactChange {
  @NotNull
  private Integer userId;

  @Valid
  @NotEmpty
  private List<Contact> contacts;

  // for deserialization
  public ContactChange() {
  }

  public ContactChange(Integer userId, List<Contact> contacts) {
    this.userId = userId;
    this.contacts = contacts;
  }

  /**
   * Get the requesting user ID
   *
   * @return user id
   */
  public Integer getUserId() {
    return userId;
  }

  /**
   * Get the contacts for the request
   *
   * @return list of contacts
   */
  public List<Contact> getContacts() {
    return contacts;
  }

}
