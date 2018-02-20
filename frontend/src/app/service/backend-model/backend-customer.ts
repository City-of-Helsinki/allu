import {BackendPostalAddress} from './backend-postal-address';

export interface BackendCustomer {
  id: number;
  type: string;
  name: string;
  registryKey: string;
  ovt: string;
  invoicingOperator: string;
  postalAddress: BackendPostalAddress;
  email: string;
  phone: string;
  active: boolean;
  sapCustomerNumber?: string;
  invoicingProhibited?: boolean;
}
