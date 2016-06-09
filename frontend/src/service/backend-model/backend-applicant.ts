import {BackendPerson} from './backend-person';
import {BackendOrganization} from './backend-organization';

export interface BackendApplicant {
  id: number;
  type: string;
  person: BackendPerson;
  organization: BackendOrganization;
}
