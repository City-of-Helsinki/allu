
import {Person} from '../common/person';
import {Organization} from '../common/organization';
import {ApplicantDetails} from '../common/applicant-details';

export class Applicant {

  constructor()
  constructor(
    id: number,
    type: string,
    representative: boolean,
    person: Person,
    organization: Organization)
  constructor(
    public id?: number,
    public type?: string,
    public representative?: boolean,
    public person?: Person,
    public organization?: Organization) {}

  get details(): ApplicantDetails {
    if (this.person) {
      return this.person;
    } else if (this.organization) {
      return this.organization;
    } else {
      throw new Error('No details for applicant');
    }
  }
}
