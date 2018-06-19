import {Application} from '../../model/application/application';
import {Customer} from '../../model/customer/customer';

export class InformationRequestResult {
  constructor(
    public informationRequestId?: number,
    public application?: Application,
    public invoiceCustomer?: Customer) {}
}
