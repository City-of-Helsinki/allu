import {FormBuilder, Validators} from '@angular/forms';
import {CustomerForm} from '../../../customerregistry/customer/customer.form';

export class InvoicingInfoForm {
  constructor(
    public id?: number,
    public invoiceRecipient?: CustomerForm,
    public notBillable?: boolean,
    public notBillableReason?: string,
    public depositAmount?: number,
    public depositReason?: string,
    public depositStatus?: string,
    public customerReference?: string,
    public invoicingDate?: Date) {
    this.invoiceRecipient = invoiceRecipient || new CustomerForm();
  }

  static initialForm(fb: FormBuilder): any {
    return fb.group({
      id: undefined,
      invoiceRecipient: CustomerForm.initialForm(fb),
      notBillable: [false],
      notBillableReason: [undefined],
      depositAmount: [{value: undefined, disabled: true}],
      depositReason: [{value: undefined, disabled: true}],
      depositStatus: [undefined],
      customerReference: [undefined],
      invoicingDate: [undefined]
    });
  }
}
