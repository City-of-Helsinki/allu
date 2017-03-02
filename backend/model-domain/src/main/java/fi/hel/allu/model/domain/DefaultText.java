package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.DefaultTextType;

/**
 * Default texts hold strings that users need to write in applications very often. Helps to avoid copy&pasting from other sources.
 */
public class DefaultText {
  private Integer id;
  private ApplicationType applicationType;
  private DefaultTextType textType;
  private String textValue;

  public DefaultText() {
    // for (de)serialization
  }

  public DefaultText(Integer id, ApplicationType applicationType, DefaultTextType textType, String textValue) {
    this.id = id;
    this.applicationType = applicationType;
    this.textType = textType;
    this.textValue = textValue;
  }

  /**
   * Database id.
   *
   * @return  Database id.
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * The type of the application this default text is bound to.
   *
   * @return  The type of the application this default text is bound to.
   */
  public ApplicationType getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(ApplicationType applicationType) {
    this.applicationType = applicationType;
  }

  /**
   * The type of the text such as "ELECTRICITY" for cable reports.
   *
   * @return  The type of the text.
   */
  public DefaultTextType getTextType() {
    return textType;
  }

  public void setTextType(DefaultTextType textType) {
    this.textType = textType;
  }

  /**
   * Value of the default text i.e. the string that may be copy&pasted in UI.
   *
   * @return  Value of the default text.
   */
  public String getTextValue() {
    return textValue;
  }

  public void setTextValue(String textValue) {
    this.textValue = textValue;
  }
}
