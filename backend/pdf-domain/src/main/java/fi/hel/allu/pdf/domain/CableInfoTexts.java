package fi.hel.allu.pdf.domain;

import java.util.List;

/**
 * Object for storing the printable contents of one cable info entry for
 * decisions:
 */
public class CableInfoTexts {
  private String type;
  private List<String> text;

  public CableInfoTexts(String type, List<String> text) {
    this.type = type;
    this.text = text;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<String> getText() {
    return text;
  }

  public void setText(List<String> text) {
    this.text = text;
  }
}