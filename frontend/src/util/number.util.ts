const CENTS = 100;

export class NumberUtil {

  static isDefined(num: number): boolean {
    return !!num || (num === 0);
  }

  static isBetween(val: number, min: number, max: number): boolean {
    return NumberUtil.isDefined(val) && (min <= val) && (val <= max);
  }

  static toEuros(cents: number): number {
    return NumberUtil.isDefined(cents) ? cents / CENTS : undefined;
  }

  static toCents(euros: number): number {
    return NumberUtil.isDefined(euros) ? euros * CENTS : undefined;
  }
}
