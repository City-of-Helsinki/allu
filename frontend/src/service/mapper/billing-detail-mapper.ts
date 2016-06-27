import {PostalAddress} from '../../model/common/postal-address';
import {BackendBillingDetail} from '../backend-model/backend-billing-detail';
import {BillingDetail} from '../../model/application/billing-detail';


export class BillingDetailMapper {

  public static mapBackend(backendBillingDetail: BackendBillingDetail): BillingDetail {
    if (!backendBillingDetail) {
      return undefined;
    }
    let postalAddress = new PostalAddress(
      backendBillingDetail.postalAddress.streetAddress,
      backendBillingDetail.postalAddress.postalCode,
      backendBillingDetail.postalAddress.city);
    return new BillingDetail(backendBillingDetail.type,
      backendBillingDetail.country,
      postalAddress,
      backendBillingDetail.workNumber,
      backendBillingDetail.reference);
  }

  public static mapFrontend(billingDetail: BillingDetail): BackendBillingDetail {
    return (billingDetail) ?
    {
      type: billingDetail.type,
      country: billingDetail.country,
      postalAddress:
        { streetAddress: billingDetail.postalAddress.streetAddress,
          postalCode: billingDetail.postalAddress.postalCode,
          city: billingDetail.postalAddress.city },
      workNumber: billingDetail.workNumber,
      reference: billingDetail.reference
    } : undefined;
  }
}
