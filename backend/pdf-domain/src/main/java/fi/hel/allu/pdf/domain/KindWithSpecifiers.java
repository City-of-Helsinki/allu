package fi.hel.allu.pdf.domain;

import java.util.List;

public class KindWithSpecifiers {
  private String kind;
  private List<String> specifiers;

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public List<String> getSpecifiers() {
    return specifiers;
  }

  public void setSpecifiers(List<String> specifiers) {
    this.specifiers = specifiers;
  }
}
