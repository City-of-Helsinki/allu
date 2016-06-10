import {BackendPostalAddress} from './backend-postal-address';
export interface BackendPerson {
  id: number;
  name: string;
  postalAddress: BackendPostalAddress;
  email: string;
  phone: string;
  ssn: string;
}
