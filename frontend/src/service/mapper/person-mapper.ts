import {PostalAddress} from '../../model/common/postal-address';
import {BackendPerson} from '../backend-model/backend-person';
import {Person} from '../../model/common/person';

export class PersonMapper {

  public static mapBackend(backendPerson: BackendPerson): Person {
    if (!backendPerson) {
      return undefined;
    }
    let postalAddress = new PostalAddress(backendPerson.streetAddress, backendPerson.postalCode, backendPerson.city);
    return new Person(backendPerson.id, backendPerson.name, postalAddress, backendPerson.email, backendPerson.phone, backendPerson.ssn);
  }

  public static mapFrontend(person: Person): BackendPerson {
    return (person) ?
    {
      id: person.id,
      name: person.name,
      streetAddress: person.postalAddress.streetAddress,
      postalCode: person.postalAddress.postalCode,
      city: person.postalAddress.city,
      email: person.email,
      phone: person.phone,
      ssn: person.ssn
    } : undefined;
  }
}
