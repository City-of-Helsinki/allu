package fi.hel.allu.pdf.domain;

/**
 * Object for storing the printable contents of one cable info entry for
 * decisions:
 */
public class CableInfoTexts {
  private String type;
  private String text;

  public CableInfoTexts() {
  }

  public CableInfoTexts(String type, String text) {
    this.type = type;
    this.text = text;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
