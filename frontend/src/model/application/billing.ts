import {PostalAddress} from '../common/postal-address';

export class Billing {
  type: string;
  postalAddress: PostalAddress;
  workNumber: number;
  reference: number;
  sales: boolean;
}
