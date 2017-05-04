import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InvoiceUnit} from '../../../model/application/invoice/invoice-unit';
import {InvoiceRow} from '../../../model/application/invoice/invoice-row';

export class InvoiceRowForm {
  constructor(
    public unit?: string,
    public quantity?: number,
    public rowText?: string,
    public unitPrice?: number,
    public netPrice?: number,
    public manuallySet?: boolean
  ) {}

  public static formGroup(fb: FormBuilder, row: InvoiceRow = new InvoiceRow()): FormGroup {
    return fb.group({
      unit: [InvoiceUnit[row.unit], Validators.required],
      quantity: [row.quantity, Validators.required],
      rowText: [row.rowText, Validators.required],
      unitPrice: [row.unitPriceEuro],
      netPrice: [row.netPriceEuro],
      manuallySet: [row.manuallySet]
    });
  }

  public static toInvoiceRow(form: InvoiceRowForm): InvoiceRow {
    let row = new InvoiceRow(
      InvoiceUnit[form.unit],
      form.quantity,
      form.rowText
    );

    row.unitPriceEuro = form.unitPrice;
    row.netPriceEuro = form.netPrice;
    row.manuallySet = form.manuallySet;
    return row;
  }

  public static toFormValue(row: InvoiceRow): InvoiceRowForm {
    return new InvoiceRowForm(
      InvoiceUnit[row.unit],
      row.quantity,
      row.rowText,
      row.unitPriceEuro,
      row.netPriceEuro,
      row.manuallySet
    );
  }
}
