export class ApplicationPricing {
  constructor()
  constructor(calculatedPrice: number)
  constructor(calculatedPrice: number, customPrice: number, reason: string)
  constructor(public calculatedPrice?: number, public customPrice?: number, public reason?: string) {}
}
