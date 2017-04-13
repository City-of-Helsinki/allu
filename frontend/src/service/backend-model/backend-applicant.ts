import {BackendPostalAddress} from './backend-postal-address';

export interface BackendApplicant {
  id: number;
  type: string;
  representative: boolean;
  name: string;
  registryKey: string;
  postalAddress: BackendPostalAddress;
  email: string;
  phone: string;
  active: boolean;
}
