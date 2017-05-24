import {PostalAddress} from './postal-address';

export interface CustomerDetails {
  id?: number;
  name?: string;
  registryKey?: string;
  postalAddress?: PostalAddress;
  email?: string;
  phone?: string;
}
