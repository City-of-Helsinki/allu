import {NumberUtil} from '@util/number.util';
import {ChargeBasisUnit} from './charge-basis-unit';
import {ChargeBasisType} from './charge-basis-type';

export class ChargeBasisEntry {
  constructor(
    public id?: number,
    public type?: ChargeBasisType,
    public unit?: ChargeBasisUnit,
    public quantity?: number,
    public text?: string,
    public unitPrice?: number,
    public netPrice?: number,
    public manuallySet?: boolean,
    public tag?: string,
    public referredTag?: string,
    public explanation: string[] = [],
    public locked?: boolean
  ) {}

  get uiQuantity(): number {
    return this.negateQuantity() ? -this.quantity : this.quantity;
  }

  set uiQuantity(quantity: number) {
    this.quantity = this.negateQuantity() ? -quantity : quantity;
    this.netPrice = this.quantity * this.unitPrice;
  }

  get unitPriceEuro(): number {
    const unitPrice = NumberUtil.toEuros(this.unitPrice);
    return this.negatePrice() ? -unitPrice : unitPrice;
  }

  set unitPriceEuro(euros: number) {
    const unitPrice = NumberUtil.toCents(euros);
    this.unitPrice = this.negatePrice() ? -unitPrice : unitPrice;
    this.netPrice = this.quantity * this.unitPrice;
  }

  get netPriceEuro(): number {
    return NumberUtil.toEuros(this.netPrice);
  }

  private negatePrice(): boolean {
    return this.type === ChargeBasisType.DISCOUNT && this.unit === ChargeBasisUnit.PIECE;
  }

  private negateQuantity(): boolean {
    return this.type === ChargeBasisType.DISCOUNT && this.unit === ChargeBasisUnit.PERCENT;
  }
}
