import {ChargeBasisUnit} from './charge-basis-unit';
import {NumberUtil} from '../../../util/number.util';
import {ChargeBasisType} from './charge-basis-type';

export const DEFAULT_FEE_CENTS = 50000;

export class ChargeBasisEntry {
  constructor(
    public type?: ChargeBasisType,
    public unit?: ChargeBasisUnit,
    public quantity?: number,
    public text?: string,
    public unitPrice?: number,
    public netPrice?: number,
    public manuallySet?: boolean,
    public tag?: string,
    public referredTag?: string,
    public explanation: string[] = []
  ) {
    quantity = quantity || ChargeBasisUnit.PIECE;
  }

  get uiUnit(): string {
    return this.unit ? ChargeBasisUnit[this.unit] : undefined;
  }

  set uiUnit(unit: string) {
    this.unit = unit ? ChargeBasisUnit[unit] : undefined;
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
