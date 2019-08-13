import {BackendPostalAddress} from './backend-postal-address';
import {CustomerType} from '@model/customer/customer-type';

export interface BackendCustomer {
  id: number;
  type: CustomerType;
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
  invoicingOnly: boolean;
  country: string;
  projectIdentifierPrefix?: string;
}

export interface SearchResultCustomer {
  id: number;
  name: string;
}
