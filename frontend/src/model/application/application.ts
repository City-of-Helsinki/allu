import {Applicant} from './applicant';
import {Project} from './project';
import {Contact} from './contact';
import {Person} from '../common/person';
import {Organization} from '../common/organization';
import {PostalAddress} from '../common/postal-address';
import {Location} from '../common/location';
import {ApplicationTypeData} from './type/application-type-data';
import {OutdoorEvent} from './type/outdoor-event';
import {StructureMeta} from './structure-meta';
import {AttachmentInfo} from './attachment-info';


export class Application {

  constructor(
    public id: number,
    public project: Project,
    public handler: string,
    public status: string,
    public type: string,
    public name: string,
    public event: ApplicationTypeData,
    public metadata: StructureMeta,
    public creationTime: Date,
    public applicant: Applicant,
    public contactList: Array<Contact>,
    public location: Location,
    public attachmentList: Array<AttachmentInfo>) {}

  public static emptyApplication(): Application {
    let applicant = new Applicant(undefined, undefined, false, undefined, undefined);
    let contactPostalAddress = new PostalAddress(undefined, undefined, undefined);
    let contactPerson = new Person(undefined, undefined, undefined, contactPostalAddress, undefined, undefined);
    let contact = new Contact(undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined);
    let applicationTypeData = new OutdoorEvent(
      undefined,
      undefined,
      undefined,
      'OUTDOOREVENT',
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined);
    return new
      Application(
        undefined,
        undefined,
        undefined,
        undefined,
        'OUTDOOREVENT',
        undefined,
        applicationTypeData,
        undefined,
        undefined,
        applicant,
        [contact],
        undefined,
        undefined);
  }

  public static preFilledApplication(): Application {
    let applicantPostalAddress = new PostalAddress('Mikonkatu 15 B', '00200', 'Helsinki');
    let applicantPerson = new Person(undefined, 'hakija ihminen', '020202-1234', applicantPostalAddress, 'hakija@ihminen.fi', '0201234567');
    let applicantOrganization = new Organization(
      undefined,
      'Hakija Inc.',
      '123456-88',
      applicantPostalAddress,
      'hakijainc@hotmail.com',
      '112');
    let applicant = new Applicant(undefined, 'COMPANY', true, undefined, applicantOrganization);
    // let applicant = new Applicant(undefined, 'PERSON', true, applicantPerson, undefined);
    let contactPostalAddress = new PostalAddress('Mikonkatu 15 C', '00300', 'Helsinki');
    let contactPerson = new Person(undefined, 'kontakti ihminen', '030303-1234', contactPostalAddress, 'kontakti@ihminen.fi', '0301234567');
    let contact = new Contact(
      undefined,
      undefined,
      'Kontakti Ihminen',
      'Mikonkatu 15 C',
      '00300',
      'Helsinki',
      'kontakti@ihminen.fi',
      '0301234567');
    let applicationTypeData = new OutdoorEvent(
      'Promootio',
      'Tapahtuman tavoitteena on saada ulkoilmatapahtumat tutuksi ihmisille.',
      'url',
      'OUTDOOREVENT',
      new Date(),
      new Date('2016-12-18T10:24:06.565+03:00'),
      'Tapahtuma-ajalla ei ole poikkeuksia',
      100,
      0,
      'DefenceOrPolice',
      true,
      false,
      true,
      'Tapahtumassa saattaa olla elintarviketoimijoita',
      'Tapahtumassa ei luultavimmin ole markkinointitoimintaa',
      54,
      'Paikalle rakennetaan linna',
      undefined,
      new Date());
    return new Application(
      undefined,
      undefined,
      'TestHandler',
      undefined,
      'OUTDOOREVENT',
      'Ulkoilmatapahtumat tutuksi!',
      applicationTypeData,
      undefined,
      undefined,
      applicant,
      [contact],
      undefined,
      undefined);
  }
}
