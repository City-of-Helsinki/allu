package fi.hel.allu.external.domain;

import fi.hel.allu.common.domain.types.ApplicationTagType;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Allu application tag, which is exposed to external users.
 */
public class ApplicationTagExt {

  @NotNull
  private ApplicationTagType type;
  @NotNull
  private ZonedDateTime creationTime;

  public ApplicationTagExt() {
    // JSON serialization
  }

  public ApplicationTagExt(ApplicationTagType type, ZonedDateTime creationTime) {
    this.type = type;
    this.creationTime = creationTime;
  }

  /**
   * Returns the type of the tag.
   *
   * @return  the type of the tag.
   */
  public ApplicationTagType getType() {
    return type;
  }

  public void setType(ApplicationTagType type) {
    this.type = type;
  }

  /**
   * Returns the time tag was created.
   *
   * @return  the time tag was created.
   */
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }
}
