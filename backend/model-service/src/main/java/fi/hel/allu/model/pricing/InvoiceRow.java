package fi.hel.allu.model.pricing;

public class InvoiceRow {
  private LineType lineType;
  private long value;

  public enum LineType {
    BASE_CHARGE, DAILY_CHARGE, TOTAL_CHARGE, STRUCTURE_CHARGE, AREA_CHARGE
  };

  public InvoiceRow(LineType lineType, long value) {
    this.lineType = lineType;
    this.value = value;
  }
}
