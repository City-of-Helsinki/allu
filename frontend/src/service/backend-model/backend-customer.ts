import {BackendPostalAddress} from './backend-postal-address';

export interface BackendCustomer {
  id: number;
  type: string;
  name: string;
  registryKey: string;
  postalAddress: BackendPostalAddress;
  email: string;
  phone: string;
  active: boolean;
}
