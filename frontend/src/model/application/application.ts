import {Applicant} from './applicant';
import {Project} from './project';
import {Contact} from './contact';
import {PostalAddress} from '../common/postal-address';
import {Location} from '../common/location';
import {ApplicationTypeData} from './type/application-type-data';
import {OutdoorEvent} from './outdoor-event/outdoor-event';
import {StructureMeta} from './structure-meta';
import {AttachmentInfo} from './attachment-info';
import {TimeUtil} from '../../util/time.util';
import {User} from '../common/user';
import {EventNature} from './outdoor-event/event-nature';
import {LocationState} from '../../service/application/location-state';
import {ApplicationType} from './type/application-type';
import {Some} from '../../util/option';


export class Application {

  constructor()
  constructor(
    id: number,
    applicationId: string,
    project: Project,
    handler: User,
    status: string,
    type: string,
    name: string,
    event: ApplicationTypeData,
    metadata: StructureMeta,
    creationTime: Date,
    startTime: Date,
    endTime: Date,
    applicant: Applicant,
    contactList: Array<Contact>,
    location: Location,
    attachmentList: Array<AttachmentInfo>)
  constructor(
    public id?: number,
    public applicationId?: string,
    public project?: Project,
    public handler?: User,
    public status?: string,
    public type?: string,
    public name?: string,
    public event?: ApplicationTypeData,
    public metadata?: StructureMeta,
    public creationTime?: Date,
    public startTime?: Date,
    public endTime?: Date,
    public applicant?: Applicant,
    public contactList?: Array<Contact>,
    public location?: Location,
    public attachmentList?: Array<AttachmentInfo>) {
    this.contactList = contactList || [new Contact()];
  }

  public static prefilledApplication(): Application {
    let applicantPostalAddress = new PostalAddress('Mikonkatu 15 B', '00200', 'Helsinki');
    let applicant = new Applicant(
      undefined, 'COMPANY', true, 'Hakija Inc.', '123456-88', applicantPostalAddress, 'hakijainc@hotmail.com', '112');

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
      EventNature[EventNature.PUBLIC_FREE],
      'Tapahtuman tavoitteena on saada ulkoilmatapahtumat tutuksi ihmisille.',
      'url',
      'OUTDOOREVENT',
      'EVENT',
      undefined,
      undefined,
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
      undefined);

    let app = new Application();
    app.handler = undefined;
    app.type = 'OUTDOOREVENT';
    app.name = 'Ulkoilmatapahtumat tutuksi!';
    app.event = applicationTypeData;
    app.contactList = [contact];
    app.applicant = applicant;
    app.startTime = undefined;
    app.endTime = undefined;

    return app;
  }

  public static fromLocationState(locationState: LocationState): Application {
    let app = new Application();

    app.location = locationState.location;

    // TODO: mismatch here. Date+time should be used in location too.
    let defaultDate = new Date();
    app.startTime = locationState.startDate || defaultDate;
    app.endTime = TimeUtil.getEndOfDay(locationState.endDate || defaultDate);

    app.type = Some(locationState.applicationType).map(type => ApplicationType[type]).orElse(undefined);
    return app;
  }

  /*
   * Getters and setters for supporting pickadate editing in UI.
   */
  get uiApplicationCreationTime(): string {
    return TimeUtil.getUiDateString(this.creationTime);
  }

  public get uiStartTime(): string {
    return TimeUtil.getUiDateString(this.startTime);
  }

  public set uiStartTime(dateString: string) {
    this.startTime = TimeUtil.getDateFromUi(dateString);
  }

  public get uiEndTime(): string {
    return TimeUtil.getUiDateString(this.endTime);
  }

  public set uiEndTime(dateString: string) {
    this.endTime = TimeUtil.getDateFromUi(dateString);
  }

  public hasGeometry(): boolean {
    return !!this.location
      && !!this.location.geometry
      && this.location.geometry.geometries.length > 0;
  }
}
