import {BackendOrganization} from './backend-organization';
import {BackendPerson} from './backend-person';

export interface BackendContact {
  id: number;
  organizationId: number;
  name: string;
  streetAddress: string;
  postalCode: string;
  city: string;
  email: string;
  phone: string;
}
