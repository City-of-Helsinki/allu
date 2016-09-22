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
import {TimeUtil} from '../../util/time.util';


export class Application {

  constructor()
  constructor(
    id: number,
    project: Project,
    handler: string,
    status: string,
    type: string,
    name: string,
    event: ApplicationTypeData,
    metadata: StructureMeta,
    creationTime: Date,
    applicant: Applicant,
    contactList: Array<Contact>,
    location: Location,
    attachmentList: Array<AttachmentInfo>)
  constructor(
    public id?: number,
    public project?: Project,
    public handler?: string,
    public status?: string,
    public type?: string,
    public name?: string,
    public event?: ApplicationTypeData,
    public metadata?: StructureMeta,
    public creationTime?: Date,
    public applicant?: Applicant,
    public contactList?: Array<Contact>,
    public location?: Location,
    public attachmentList?: Array<AttachmentInfo>) {

    this.location = location || new Location();
    this.applicant = applicant || new Applicant();
  }

  public static prefilledApplication(): Application {
    let applicantPostalAddress = new PostalAddress('Mikonkatu 15 B', '00200', 'Helsinki');
    let applicantOrganization = new Organization(
      undefined,
      'Hakija Inc.',
      '123456-88',
      applicantPostalAddress,
      'hakijainc@hotmail.com',
      '112');
    let applicant = new Applicant(undefined, 'COMPANY', true, undefined, applicantOrganization);

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
      undefined,
      false,
      false,
      false,
      true,
      'Tapahtumassa saattaa olla elintarviketoimijoita',
      'Tapahtumassa ei luultavimmin ole markkinointitoimintaa',
      54,
      'Paikalle rakennetaan linna',
      undefined,
      new Date());

    let app = new Application();
    app.handler = 'TestHandler';
    app.type = 'OUTDOOREVENT';
    app.name = 'Ulkoilmatapahtumat tutuksi!';
    app.event = applicationTypeData;
    app.contactList = [contact];
    app.applicant = applicant;
    return app;
  }

  get uiApplicationCreationTime(): string {
    return TimeUtil.getUiDateString(this.creationTime);
  }
}
