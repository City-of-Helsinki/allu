package fi.hel.allu.model.pricing;

public class InvoiceRow {
  private RowType rowType;
  private long value;

  public enum RowType {
    BASE_CHARGE, DAILY_CHARGE, TOTAL_CHARGE, STRUCTURE_CHARGE, AREA_CHARGE
  };

  public InvoiceRow(RowType rowType, long value) {
    this.rowType = rowType;
    this.value = value;
  }
}
