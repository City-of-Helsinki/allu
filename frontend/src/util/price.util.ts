const EUROS_TO_CENTS = 100;
const PERCENTAGE = 100;

export class PriceUtil {

  static discountPercentage(value: number): number {
    return this.percentage(-value);
  }

  static percentage(value: number): number {
    return (PERCENTAGE + value) / PERCENTAGE;
  }

  static toEuros(cents: number): number {
    return cents ? cents / EUROS_TO_CENTS : undefined;
  }

  static toCents(euros: number): number {
    return euros ? euros * EUROS_TO_CENTS : undefined;
  }
}
