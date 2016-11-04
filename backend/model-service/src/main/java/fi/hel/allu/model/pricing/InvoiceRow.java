package fi.hel.allu.model.pricing;

public class InvoiceRow {
  private RowType rowType;
  private long value;

  public enum RowType {
    BASE_CHARGE, DAILY_CHARGE, TOTAL_CHARGE, STRUCTURE_CHARGE, AREA_CHARGE, FREE_EVENT, HEAVY_STRUCTURE, SALES_ACTIVITY, ECO_COMPASS
  };

  public InvoiceRow(RowType rowType, long value) {
    this.rowType = rowType;
    this.value = value;
  }
}
