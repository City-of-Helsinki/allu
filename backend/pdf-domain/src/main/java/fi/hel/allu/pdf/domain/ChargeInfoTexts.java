package fi.hel.allu.pdf.domain;

/**
 * Object for storing printable contents of one line in decision's charge
 * itemization section
 */
public class ChargeInfoTexts {
  private int level;
  private String text;
  private String[] explanation;
  private String quantity;
  private String unitPrice;
  private String netPrice;

  public ChargeInfoTexts() {
  }

  public ChargeInfoTexts(int level, String text, String[] explanation, String quantity, String unitPrice,
      String netPrice) {
    this.level = level;
    this.text = text;
    this.explanation = explanation;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.netPrice = netPrice;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String[] getExplanation() {
    return explanation;
  }

  public void setExplanation(String[] explanation) {
    this.explanation = explanation;
  }

  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  public String getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(String unitPrice) {
    this.unitPrice = unitPrice;
  }

  public String getNetPrice() {
    return netPrice;
  }

  public void setNetPrice(String netPrice) {
    this.netPrice = netPrice;
  }

}
