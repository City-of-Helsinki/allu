import {BackendPostalAddress} from './backend-postal-address';
export interface BackendDistributionEntry {
  id: number;
  name: string;
  distributionType: string;
  email: string;
  postalAddress?: BackendPostalAddress;
}
