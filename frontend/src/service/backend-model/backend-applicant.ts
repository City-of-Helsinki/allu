import {BackendPerson} from './backend-person';
import {BackendOrganization} from './backend-organization';

export interface BackendApplicant {
  id: number;
  type: string;
  representative: boolean;
  person: BackendPerson;
  organization: BackendOrganization;
}
