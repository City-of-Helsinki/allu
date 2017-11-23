import {PostalAddress} from '../../common/postal-address';
import {CustomerType} from '../../customer/customer-type';
import {InvoicePartition} from './ivoice-partition';

export class InvoicingInfo {
  constructor(
    public id?: number,
    public type?: CustomerType,
    public name?: string,
    public registryKey?: string,
    public postalAddress?: PostalAddress,
    public email?: string,
    public phone?: string,
    public workId?: string,
    public invoiceReference?: string,
    public deposit?: number,
    public partition?: InvoicePartition,
    public notBillable?: boolean,
    public notBillableReason?: string) {
    this.postalAddress = postalAddress || new PostalAddress();
  }
}
