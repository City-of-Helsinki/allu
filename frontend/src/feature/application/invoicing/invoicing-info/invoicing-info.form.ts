import {FormBuilder} from '@angular/forms';
import {CustomerForm} from '../../../customerregistry/customer/customer.form';

export class InvoicingInfoForm {
  constructor(
    public id?: number,
    public invoiceRecipient?: CustomerForm,
    public notBillable?: boolean,
    public notBillableReason?: string) {
    this.invoiceRecipient = invoiceRecipient || new CustomerForm();
  }

  static initialForm(fb: FormBuilder): any {
    return fb.group({
      id: undefined,
      invoiceRecipient: CustomerForm.initialForm(fb),
      notBillable: [false],
      notBillableReason: [undefined]
    });
  }
}
