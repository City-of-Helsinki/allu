import {Applicant} from './applicant';
import {Project} from './project';
import {Contact} from './contact';
import {Customer} from '../common/customer';
import {Person} from '../common/person';
import {PostalAddress} from '../common/postal-address';

export class Application {

  constructor(
    public id: number,
    public project: Project,
    public handler: string,
    public customer: Customer,
    public status: string,
    public type: string,
    public name: string,
    public creationTime: Date,
    public applicant: Applicant,
    public contactList: Array<Contact>,
    public location: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>) {}

  public static emptyApplication(): Application {
    let customerPostalAddress = new PostalAddress(undefined, undefined, undefined);
    let customerPerson = new Person(undefined, undefined, customerPostalAddress, undefined, undefined, undefined);
    let customer = new Customer(undefined, 'Person', '123', customerPerson, undefined);
    let applicantPostalAddress = new PostalAddress(undefined, undefined, undefined);
    let applicantPerson = new Person(undefined, undefined, applicantPostalAddress, undefined, undefined, undefined);
    let applicant = new Applicant(undefined, applicantPerson, undefined);
    let contactPostalAddress = new PostalAddress(undefined, undefined, undefined);
    let contactPerson = new Person(undefined, undefined, contactPostalAddress, undefined, undefined, undefined);
    let contact = new Contact(undefined, contactPerson, undefined);
    return new
      Application(undefined, undefined, undefined, customer, undefined, undefined, undefined, undefined, applicant, [contact], undefined);
  }
}
