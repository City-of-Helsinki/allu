
import {Person} from '../common/person';
import {Organization} from '../common/organization';

export class Applicant {
  constructor(
    public id: number,
    public type: string,
    public representative: boolean,
    public person: Person,
    public organization: Organization) {}
}
