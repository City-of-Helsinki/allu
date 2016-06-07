import {Organization} from '../common/organization';
import {Person} from '../common/person';

export class Contact {
  constructor(public id: number, public person: Person, public organization: Organization) {}
}
