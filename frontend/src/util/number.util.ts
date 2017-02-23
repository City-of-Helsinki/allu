export class NumberUtil {
  static isDefined(num: number): boolean {
    return !!num || (num === 0);
  }
}
