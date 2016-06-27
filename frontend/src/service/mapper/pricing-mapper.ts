
import {BackendPricing} from '../backend-model/backend-pricing';
import {Pricing} from '../../model/application/pricing';
export class PricingMapper {

  public static mapBackend(backendPricing: BackendPricing): Pricing {
    return (backendPricing) ?
      new Pricing(backendPricing.noPrice, backendPricing.salesActivity, backendPricing.ecoCompass) : undefined;
  }

  public static mapFrontend(pricing: Pricing): BackendPricing {
    return (pricing) ? {
      noPrice: pricing.noPrice,
      salesActivity: pricing.salesActivity,
      ecoCompass: pricing.ecoCompass
    } : undefined;
  }
}
