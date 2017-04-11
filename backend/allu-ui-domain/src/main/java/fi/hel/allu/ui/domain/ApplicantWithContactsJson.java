package fi.hel.allu.ui.domain;

import java.util.List;

/**
 * Wrapper class for communicating changes to applicant and its related contacts to and back from frontend.
 */
public class ApplicantWithContactsJson {
  ApplicantJson applicant;
  List<ContactJson> contacts;

  /**
   * Created or updated applicant.
   *
   * @return  Created or updated applicant. May be <code>null</code> in case applicant is not changed, but related contacts are (the
   *          id of the applicant must be communicated by other means than as instance variable).
   */
  public ApplicantJson getApplicant() {
    return applicant;
  }

  public void setApplicant(ApplicantJson applicant) {
    this.applicant = applicant;
  }

  /**
   * Created or updated contacts. If a contact is neither created nor updated, it should not appear on the list!
   *
   * @return  List of created or updated contacts. If contact has an id, it's considered as an update.
   */
  public List<ContactJson> getContacts() {
    return contacts;
  }

  public void setContacts(List<ContactJson> contacts) {
    this.contacts = contacts;
  }
}
