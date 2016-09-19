
import {Person} from '../common/person';
import {Organization} from '../common/organization';

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
}
