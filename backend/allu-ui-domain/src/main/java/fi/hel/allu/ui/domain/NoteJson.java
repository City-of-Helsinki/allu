package fi.hel.allu.ui.domain;

import fi.hel.allu.common.types.ApplicationType;

/**
 * JSON DAO for notes
 */
public class NoteJson extends ApplicationExtensionJson {

  private Boolean reoccurring;
  private String description;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.NOTE;
  }

  /**
   * Is this a reoccurring note?
   * 
   * @return true if the note is reoccurring
   */
  public Boolean getReoccurring() {
    return reoccurring;
  }

  public void setReoccurring(Boolean reoccurring) {
    this.reoccurring = reoccurring;
  }

  /**
   * Returns the description of the note.
   *
   * @return the description of the note.
   */
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
