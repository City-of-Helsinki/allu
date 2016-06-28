import {PostalAddress} from '../../model/common/postal-address';
import {BackendPerson} from '../backend-model/backend-person';
import {Person} from '../../model/common/person';

export class PersonMapper {

  public static mapBackend(backendPerson: BackendPerson): Person {
    if (!backendPerson) {
      return undefined;
    }
    let postalAddress = new PostalAddress(
      backendPerson.postalAddress.streetAddress, backendPerson.postalAddress.postalCode, backendPerson.postalAddress.city);
    return new Person(backendPerson.id, backendPerson.name, backendPerson.ssn, postalAddress, backendPerson.email, backendPerson.phone);
  }

  public static mapFrontend(person: Person): BackendPerson {
    return (person) ?
    {
      id: person.id,
      name: person.name,
      ssn: person.ssn,
      postalAddress:
        { streetAddress: person.postalAddress.streetAddress, postalCode: person.postalAddress.postalCode, city: person.postalAddress.city },
      email: person.email,
      phone: person.phone
    } : undefined;
  }
}
