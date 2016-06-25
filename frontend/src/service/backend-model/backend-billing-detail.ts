import {BackendPostalAddress} from './backend-postal-address';

export interface BackendBillingDetail {
  type: string;
  country: string;
  postalAddress: BackendPostalAddress;
  workNumber: string;
  reference: string;
}
