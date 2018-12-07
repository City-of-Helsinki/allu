package fi.hel.allu.pdf.domain;

public class RentalArea {
  private String areaId;
  private Boolean finished;
  private String time;
  private String address;
  private String underpass;
  private String area;
  private String paymentClass;
  private String unitPrice;
  private String quantity;
  private String days;
  private String price;
  private String additionalInfo;
  private String text;
  private String chargeBasisText;
  private Boolean firstCommon;

  public String getAreaId() {
    return areaId;
  }

  public void setAreaId(String areaId) {
    this.areaId = areaId;
  }

  public Boolean isFinished() {
    return finished;
  }

  public void setFinished(Boolean finished) {
    this.finished = finished;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getUnderpass() {
    return underpass;
  }

  public void setUnderpass(String underpass) {
    this.underpass = underpass;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getPaymentClass() {
    return paymentClass;
  }

  public void setPaymentClass(String paymentClass) {
    this.paymentClass = paymentClass;
  }

  public String getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(String unitPrice) {
    this.unitPrice = unitPrice;
  }

  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  public String getDays() {
    return days;
  }

  public void setDays(String days) {
    this.days = days;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getChargeBasisText() {
    return chargeBasisText;
  }

  public void setChargeBasisText(String chargeBasisText) {
    this.chargeBasisText = chargeBasisText;
  }

  public Boolean isFirstCommon() {
    return firstCommon;
  }

  public void setFirstCommon(Boolean firstCommon) {
    this.firstCommon = firstCommon;
  }
}
