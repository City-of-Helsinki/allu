import {BackendPostalAddress} from './backend-postal-address';
export interface BackendPerson {
  id: number;
  name: string;
  ssn: string;
  postalAddress: BackendPostalAddress;
  email: string;
  phone: string;
}
