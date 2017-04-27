import {InvoiceUnit} from './invoice-unit';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {NumberUtil} from '../../../util/number.util';

export const DEFAULT_FEE_CENTS = 50000;

export class InvoiceRow {
  constructor(
    public unit?: InvoiceUnit,
    public quantity?: number,
    public rowText?: string,
    public unitPrice?: number,
    public netPrice?: number
  ) {
    quantity = quantity || InvoiceUnit.PIECE;
  }

  get unitPriceEuro(): number {
    return NumberUtil.toEuros(this.unitPrice);
  }

  set unitPriceEuro(euros: number) {
    this.unitPrice = NumberUtil.toCents(euros);
  }

  get netPriceEuro(): number {
    return NumberUtil.toEuros(this.netPrice);
  }

  set netPriceEuro(euros: number) {
    this.netPrice =  NumberUtil.toCents(euros);
  }

  updateNetPrice(): void {
    this.netPrice = this.unitPrice * this.quantity;
  }
}
