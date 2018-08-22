import {Application} from '../../model/application/application';
import {Customer} from '../../model/customer/customer';
import {CustomerRoleType} from '@model/customer/customer-role-type';

export class InformationRequestResult {
  constructor(
    public informationRequestId?: number,
    public application?: Application,
    public invoiceCustomer?: Customer,
    public useCustomerForInvoicing?: CustomerRoleType) {}
}
