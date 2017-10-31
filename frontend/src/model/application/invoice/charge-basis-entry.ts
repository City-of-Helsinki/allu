import {ChargeBasisUnit} from './charge-basis-unit';
import {NumberUtil} from '../../../util/number.util';

export const DEFAULT_FEE_CENTS = 50000;

export class ChargeBasisEntry {
  constructor(
    public unit?: ChargeBasisUnit,
    public quantity?: number,
    public text?: string,
    public unitPrice?: number,
    public netPrice?: number,
    public manuallySet?: boolean,
    public tag?: string,
    public referredTag?: string
  ) {
    quantity = quantity || ChargeBasisUnit.PIECE;
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
