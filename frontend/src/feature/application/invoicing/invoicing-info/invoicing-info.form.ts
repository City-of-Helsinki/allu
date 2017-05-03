import {PostalAddress} from '../../../../model/common/postal-address';
import {FormBuilder, Validators} from '@angular/forms';
import {InvoicingInfo} from '../../../../model/application/invoice/invoicing-info';
import {ApplicantType} from '../../../../model/application/applicant/applicant-type';
import {InvoicePartition} from '../../../../model/application/invoice/ivoice-partition';
import {InvoicingAddressForm} from '../../../customerregistry/invoicing-address/invoicing-address.form';

export class InvoicingInfoForm {
  constructor(
    public id?: number,
    public invoicingAddress?: InvoicingAddressForm,
    public workId?: string,
    public invoiceReference?: string,
    public deposit?: number,
    public partition?: string,
    public readyForInvoicing?: string) {
    this.invoicingAddress = invoicingAddress || new InvoicingAddressForm();
  }

  static fromInvoicingInfo(info: InvoicingInfo): InvoicingInfoForm {
    return new InvoicingInfoForm(
      info.id,
      this.addressFromInvoicingInfo(info),
      info.workId,
      info.invoiceReference,
      info.deposit,
      info.partition ? InvoicePartition[info.partition] : undefined,
      info.uiReadyForInvoicing
    );
  }

  static toInvoicingInfo(form: InvoicingInfoForm): InvoicingInfo {
    let info = new InvoicingInfo();
    info.id = form.id;
    this.addressToInvoicingInfo(form.invoicingAddress, info);
    info.workId = form.workId;
    info.invoiceReference = form.invoiceReference;
    info.deposit = form.deposit;
    info.partition = form.partition ? InvoicePartition[form.partition] : undefined;
    info.uiReadyForInvoicing = form.readyForInvoicing;
    return info;
  }

  static initialForm(fb: FormBuilder): any {
    return fb.group({
      id: undefined,
      invoicingAddress: InvoicingAddressForm.initialForm(fb),
      workId: ['', Validators.required],
      invoiceReference: ['', Validators.required],
      deposit: [undefined],
      partition: ['']
    });
  }

  private static addressFromInvoicingInfo(info: InvoicingInfo): InvoicingAddressForm {
    return new InvoicingAddressForm(
      undefined,
      info.type ? ApplicantType[info.type] : undefined,
      info.name,
      info.registryKey,
      'Suomi',
      info.postalAddress || new PostalAddress(),
      info.email,
      info.phone
    );
  }

  private static addressToInvoicingInfo(form: InvoicingAddressForm, info: InvoicingInfo): void {
    info.type = form.type ? ApplicantType[form.type] : undefined;
    info.name = form.name;
    info.registryKey = form.registryKey;
    info.postalAddress = form.postalAddress;
    info.email = form.email;
    info.phone = form.phone;
  }
}
