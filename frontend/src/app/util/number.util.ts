const CENTS = 100;

export class NumberUtil {

  /**
   * Checks if given item is an existing item
   * eg. truthy and has id
   */
  static isExisting<T extends {id?: number}>(item: T): boolean {
    return !!item && NumberUtil.isDefined(item.id);
  }

  static isDefined(num: number): boolean {
    return !!num || (num === 0);
  }

  static isNumeric(num: any): boolean {
    return num !== undefined && num !== '' && !isNaN(num);
  }

  static isBetween(val: number, min: number, max: number): boolean {
    return NumberUtil.isDefined(val) && (min <= val) && (val <= max);
  }

  static toEuros(cents: number): number {
    return NumberUtil.isDefined(cents) ? cents / CENTS : undefined;
  }

  static toCents(euros: number): number {
    const cents = +(euros * CENTS).toFixed(0);
    return NumberUtil.isDefined(euros) ? cents : undefined;
  }
}
