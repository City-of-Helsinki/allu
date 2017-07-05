package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.ApplicationType;

/**
 * JSON DAO for notes
 */
public class Note extends ApplicationExtension {

  private String description;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.NOTE;
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
