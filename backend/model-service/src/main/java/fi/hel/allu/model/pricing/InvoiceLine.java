package fi.hel.allu.model.pricing;

public class InvoiceLine {
  private LineType lineType;
  private long value;

  public enum LineType {
    BASE_CHARGE, DAILY_CHARGE, TOTAL_CHARGE, STRUCTURE_CHARGE, AREA_CHARGE
  };

  public InvoiceLine(LineType lineType, long value) {
    this.lineType = lineType;
    this.value = value;
  }
}
