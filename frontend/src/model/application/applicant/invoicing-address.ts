import {PostalAddress} from '../../common/postal-address';
import {ApplicantType} from '../applicant/applicant-type';

export class InvoicingAddress {
  constructor(
    public id?: number,
    public type?: ApplicantType,
    public name?: string,
    public registryKey?: string,
    public postalAddress?: PostalAddress,
    public email?: string,
    public phone?: string,
    public noInvoicing?: boolean) {
    this.postalAddress = postalAddress || new PostalAddress();
  }
}
