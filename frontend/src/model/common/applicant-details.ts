import {PostalAddress} from './postal-address';

export interface ApplicantDetails {
  id?: number;
  name?: string;
  identifier?: string;
  postalAddress?: PostalAddress;
  email?: string;
  phone?: string;
}
