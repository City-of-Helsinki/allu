import {PostalAddress} from '../../common/postal-address';
import {ApplicantType} from '../applicant/applicant-type';
import {InvoicePartition} from './ivoice-partition';
import {TimeUtil} from '../../../util/time.util';

export class InvoicingInfo {
  constructor(
    public id?: number,
    public type?: ApplicantType,
    public name?: string,
    public registryKey?: string,
    public postalAddress?: PostalAddress,
    public email?: string,
    public phone?: string,
    public workId?: string,
    public invoiceReference?: string,
    public deposit?: number,
    public partition?: InvoicePartition,
    public readyForInvoicing?: Date) {
    this.postalAddress = postalAddress || new PostalAddress();
  }

  get uiReadyForInvoicing() {
    return TimeUtil.getUiDateString(this.readyForInvoicing);
  }

  set uiReadyForInvoicing(dateString: string) {
    this.readyForInvoicing = TimeUtil.getDateFromUi(dateString);
  }
}
