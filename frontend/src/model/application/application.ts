import {Applicant} from './applicant';
import {Project} from './project';
import {Contact} from './contact';
import {Customer} from '../common/customer';
import {Person} from '../common/person';
import {PostalAddress} from '../common/postal-address';
import {Location} from '../common/location';
import {ApplicationTypeData} from './type/application-type-data';
import {OutdoorEvent} from './type/outdoor-event';
import {BillingDetail} from './billing-detail';
import {Sales} from './sales';
import {Pricing} from './pricing';
import {Structure} from './structure';


export class Application {

  constructor(
    public id: number,
    public project: Project,
    public handler: string,
    public customer: Customer,
    public status: string,
    public type: string,
    public name: string,
    public billingDetail: BillingDetail,
    public sales: Sales,
    public event: ApplicationTypeData,
    public pricing: Pricing,
    public structure: Structure,
    public creationTime: Date,
    public applicant: Applicant,
    public contactList: Array<Contact>,
    public location: Location,
    public comments: string) {}

  public static emptyApplication(): Application {
    let customerPostalAddress = new PostalAddress(undefined, undefined, undefined);
    let customerPerson = new Person(undefined, undefined, customerPostalAddress, undefined, undefined, undefined);
    let customer = new Customer(undefined, 'Person', '123', customerPerson, undefined);
    let applicantPostalAddress = new PostalAddress(undefined, undefined, undefined);
    let applicantPerson = new Person(undefined, undefined, applicantPostalAddress, undefined, undefined, undefined);
    let applicant = new Applicant(undefined, 'Person', false, applicantPerson, undefined);
    let contactPostalAddress = new PostalAddress(undefined, undefined, undefined);
    let contactPerson = new Person(undefined, undefined, contactPostalAddress, undefined, undefined, undefined);
    let contact = new Contact(undefined, contactPerson, undefined);
    let billingDetail = new BillingDetail(undefined, undefined, new PostalAddress(undefined, undefined, undefined), undefined, undefined);
    let sales = new Sales(undefined, undefined);
    let pricing = new Pricing(undefined, undefined, undefined);
    let structure = new Structure(undefined, undefined, undefined, undefined);
    return new
      Application(
        undefined,
        undefined,
        undefined,
        customer,
        undefined,
        undefined,
        undefined,
        billingDetail,
        sales,
        undefined,
        pricing,
        structure,
        undefined,
        applicant,
        [contact],
        undefined,
        undefined);
  }

  public static preFilledApplication(): Application {
    let customerPostalAddress = new PostalAddress('Mikonkatu 15 A', '00100', 'Helsinki');
    let customerPerson = new Person(undefined, 'asiakas ihminen', customerPostalAddress, 'asiakas@ihminen.fi', '0101234567', '010101-1234');
    let customer = new Customer(undefined, 'Person', '123', customerPerson, undefined);
    let applicantPostalAddress = new PostalAddress('Mikonkatu 15 B', '00200', 'Helsinki');
    let applicantPerson = new Person(undefined, 'hakija ihminen', applicantPostalAddress, 'hakija@ihminen.fi', '0201234567', '020202-1234');
    let applicant = new Applicant(undefined, 'Person', true, applicantPerson, undefined);
    let contactPostalAddress = new PostalAddress('Mikonkatu 15 C', '00300', 'Helsinki');
    let contactPerson = new Person(undefined, 'kontakti ihminen', contactPostalAddress, 'kontakti@ihminen.fi', '0301234567', '030303-1234');
    let contact = new Contact(undefined, contactPerson, undefined);
    let billingDetail = new BillingDetail('Invoice', 'Finland', new PostalAddress('Laskutie', '00100', 'Helsinki'), 757575, 575757);
    let applicationTypeData = new OutdoorEvent(
      'Promootio',
      'Tapahtuman tavoitteena on saada ulkoilmatapahtumat tutuksi ihmisille.',
      'url',
      new Date(),
      new Date('2016-12-18T10:24:06.565+03:00'),
      'Tapahtuma-ajalla ei ole poikkeuksia',
      100,
      0);
    let sales = new Sales('Tapahtumassa saattaa olla elintarviketoimijoita', 'Tapahtumassa ei luultavimmin ole markkinointitoimintaa');
    let pricing = new Pricing('DefenceOrPolice', true, false);
    let structure = new Structure('54', 'Paikalle rakennetaan linna', undefined, new Date());
    return new Application(
      undefined,
      undefined,
      'TestHandler',
      customer,
      undefined,
      'OutdoorEvent',
      'Ulkoilmatapahtumat tutuksi!',
      billingDetail,
      sales,
      applicationTypeData,
      pricing,
      structure,
      undefined,
      applicant,
      [contact],
      undefined,
      'Hanke vaatii sijoitusluvan ennen päätöstä.');
  }
}
