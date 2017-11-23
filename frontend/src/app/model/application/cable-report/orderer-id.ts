import {NumberUtil} from '../../../util/number.util';

export class OrdererId {
  constructor()
  constructor(
    public id?: number,
    public customerRoleType?: string,
    public index?: number
  ) {}

  public matches(id: number, customerRoleType: string, index: number) {
    if (NumberUtil.isDefined(this.id)) {
      return this.id === id;
    } else {
      return this.customerRoleType === customerRoleType && this.index === index;
    }
  }

  public idOrRoleTypeMatches(id: number, customerRoleType: string) {
    if (NumberUtil.isDefined(this.id)) {
      return this.id === id;
    } else {
      return this.customerRoleType === customerRoleType;
    }
  }

  public static of(id: number, customerRoleType: string, index: number) {
    return NumberUtil.isDefined(id)
      ? OrdererId.ofId(id)
      : OrdererId.ofRoleAndIndex(customerRoleType, index);
  }

  public static ofId(id: number): OrdererId {
    let ordererId = new OrdererId();
    ordererId.id = id;
    return ordererId;
  }

  public static ofRoleAndIndex(customerRoleType: string, index: number): OrdererId {
    let ordererId = new OrdererId();
    ordererId.customerRoleType = customerRoleType;
    ordererId.index = index;
    return ordererId;
  }
}
