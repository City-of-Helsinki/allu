
import {BackendSales} from '../backend-model/backend-sales';
import {Sales} from '../../model/application/sales';
export class SalesMapper {

  public static mapBackend(backendSales: BackendSales): Sales {
    return (backendSales) ?
      new Sales(backendSales.foodProviders, backendSales.marketingProviders) : undefined;
  }

  public static mapFrontend(sales: Sales): BackendSales {
    return (sales) ? {
      foodProviders: sales.foodProviders,
      marketingProviders: sales.marketingProviders
    } : undefined;
  }
}
