import {BackendOrganization} from './backend-organization';
import {BackendPerson} from './backend-person';

export interface BackendContact {
  id: number;
  organization: BackendOrganization;
  person: BackendPerson;
}
