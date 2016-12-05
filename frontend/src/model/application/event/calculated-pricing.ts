const CENTS = 100;

export class CalculatedPricing {
  constructor()
  constructor(price: number)
  constructor(public price?: number) {}

  get euroPrice(): number {
    return this.price ? this.price / CENTS : undefined;
  }

  set euroPrice(priceInEuros: number) {
    this.price = priceInEuros ? priceInEuros * CENTS : undefined;
  }
}
