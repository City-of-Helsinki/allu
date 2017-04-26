import {InvoiceUnit} from './invoice-unit';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {NumberUtil} from '../../../util/number.util';

export class InvoiceRow {
  constructor(
    public unit?: InvoiceUnit,
    public quantity?: number,
    public rowText?: string,
    public unitPrice?: number,
    public netPrice?: number
  ) {}

  get unitPriceEuro(): number {
    return NumberUtil.toEuros(this.unitPrice);
  }

  get netPriceEuro(): number {
    return NumberUtil.toEuros(this.netPrice);
  }

  public static formGroup(fb: FormBuilder, row: InvoiceRow = new InvoiceRow()): FormGroup {
    return fb.group({
      unit: [InvoiceUnit[row.unit], Validators.required],
      quantity: [row.quantity, Validators.required],
      rowText: [row.rowText],
      unitPrice: [row.unitPriceEuro, Validators.required],
      netPrice: [row.netPriceEuro, Validators.required]
    });
  }
}
