import {Organization} from './organization';
import {Person} from './person';

export class Customer {
  constructor(public id: number, public type: string, public sapId: string, public person: Person, public organization: Organization) {}
}
