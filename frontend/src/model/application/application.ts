import {Applicant} from './applicant';
import {Project} from './project';
import {Contact} from './contact';
import {Customer} from '../common/customer';
import {Person} from '../common/person';
import {PostalAddress} from '../common/postal-address';
import {Location} from '../common/location';
import {ApplicationTypeData} from './type/application-type-data';
import {OutdoorEvent} from './type/outdoor-event';

export class Application {

  constructor(
    public id: number,
    public project: Project,
    public handler: string,
    public customer: Customer,
    public status: string,
    public type: string,
    public name: string,
    public event: ApplicationTypeData,
    public creationTime: Date,
    public applicant: Applicant,
    public contactList: Array<Contact>,
    public location: Location) {}

  public static emptyApplication(): Application {
    let customerPostalAddress = new PostalAddress(undefined, undefined, undefined);
    let customerPerson = new Person(undefined, undefined, customerPostalAddress, undefined, undefined, undefined);
    let customer = new Customer(undefined, 'Person', '123', customerPerson, undefined);
    let applicantPostalAddress = new PostalAddress(undefined, undefined, undefined);
    let applicantPerson = new Person(undefined, undefined, applicantPostalAddress, undefined, undefined, undefined);
    let applicant = new Applicant(undefined, 'Person', applicantPerson, undefined);
    let contactPostalAddress = new PostalAddress(undefined, undefined, undefined);
    let contactPerson = new Person(undefined, undefined, contactPostalAddress, undefined, undefined, undefined);
    let contact = new Contact(undefined, contactPerson, undefined);
    return new
      Application(
        undefined,
        undefined,
        undefined,
        customer,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        applicant,
        [contact],
        undefined);
  }

  public static preFilledApplication(): Application {
    let customerPostalAddress = new PostalAddress('Mikonkatu 15 A', '00100', 'Helsinki');
    let customerPerson = new Person(undefined, 'asiakas ihminen', customerPostalAddress, 'asiakas@ihminen.fi', '0101234567', '010101-1234');
    let customer = new Customer(undefined, 'Person', '123', customerPerson, undefined);
    let applicantPostalAddress = new PostalAddress('Mikonkatu 15 B', '00200', 'Helsinki');
    let applicantPerson = new Person(undefined, 'hakija ihminen', applicantPostalAddress, 'hakija@ihminen.fi', '0201234567', '020202-1234');
    let applicant = new Applicant(undefined, 'Person', applicantPerson, undefined);
    let contactPostalAddress = new PostalAddress('Mikonkatu 15 C', '00300', 'Helsinki');
    let contactPerson = new Person(undefined, 'kontakti ihminen', contactPostalAddress, 'kontakti@ihminen.fi', '0301234567', '030303-1234');
    let contact = new Contact(undefined, contactPerson, undefined);
    let applicationTypeData = new OutdoorEvent('Nature',
        'description',
        'url',
        'OutdoorEvent',
        new Date(),
        new Date('2016-12-18T10:24:06.565+03:00'),
        100);

    return new Application(
      undefined,
      undefined,
      'TestHandler',
      customer,
      undefined,
      'OutdoorEvent',
      'Testihakemus ' + Date.now(),
      applicationTypeData,
      undefined,
      applicant,
      [contact],
      undefined);
  }
}
