import {Applicant} from './applicant';
import {Contact} from './contact';
import {Location} from '../common/location';
import {ApplicationExtension} from './type/application-extension';
import {StructureMeta} from './meta/structure-meta';
import {AttachmentInfo} from './attachment/attachment-info';
import {TimeUtil} from '../../util/time.util';
import {User} from '../common/user';
import {Some} from '../../util/option';
import {Project} from '../project/project';
import {ApplicationTag} from './tag/application-tag';
import {ApplicationTagType} from './tag/application-tag-type';
import {Comment} from './comment/comment';
import {NumberUtil} from '../../util/number.util';

const CENTS = 100;

export class Application {

  constructor()
  constructor(
    id: number,
    applicationId: string,
    project: Project,
    handler: User,
    status: string,
    type: string,
    kind: string,
    metadataVersion: number,
    name: string,
    creationTime: Date,
    startTime: Date,
    endTime: Date,
    applicant: Applicant,
    contactList: Array<Contact>,
    location: Location,
    extension: ApplicationExtension,
    decisionTime: Date,
    attachmentList: Array<AttachmentInfo>,
    calculatedPrice: number,
    priceOverride: number,
    priceOverrideReason: string,
    tagList: Array<ApplicationTag>,
    comments: Array<Comment>)
  constructor(
    public id?: number,
    public applicationId?: string,
    public project?: Project,
    public handler?: User,
    public status?: string,
    public type?: string,
    public kind?: string,
    public metadataVersion?: number,
    public name?: string,
    public creationTime?: Date,
    public startTime?: Date,
    public endTime?: Date,
    public applicant?: Applicant,
    public contactList?: Array<Contact>,
    public location?: Location,
    public extension?: ApplicationExtension,
    public decisionTime?: Date,
    public attachmentList?: Array<AttachmentInfo>,
    public calculatedPrice?: number,
    public priceOverride?: number,
    public priceOverrideReason?: string,
    public applicationTags?: Array<ApplicationTag>,
    public comments?: Array<Comment>) {
    this.location = location || new Location();
    this.contactList = contactList || [new Contact()];
    this.attachmentList = attachmentList || [];
    this.applicationTags = applicationTags || [];
    this.comments = comments || [];
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
    return this.geometryCount() > 0;
  }

  public geometryCount(): number {
    return Some(this.location)
      .map(loc => loc.geometry)
      .map(g => g.geometries.length).orElse(0);
  }

  public hasFixedGeometry(): boolean {
    return Some(this.location).map(loc => loc.fixedLocationIds.length > 0).orElse(false);
  }

  public belongsToProject(projectId: number): boolean {
    return Some(this.project).map(p => p.id === projectId).orElse(false);
  }

  get calculatedPriceEuro(): number {
    return this.toEuros(this.calculatedPrice);
  }

  set calculatedPriceEuro(priceInEuros: number) {
    this.calculatedPrice = this.toCents(priceInEuros);
  }

  get priceOverrideEuro(): number {
    return this.toEuros(this.priceOverride);
  }

  set priceOverrideEuro(overrideInEuros: number) {
    this.priceOverride = this.toCents(overrideInEuros);
  }

  get waiting(): boolean {
    return this.applicationTags
      .some(tag => ApplicationTagType[tag.type] === ApplicationTagType.WAITING);
  }

  private toEuros(priceInCents: number): number {
    return NumberUtil.isDefined(priceInCents) ? priceInCents / CENTS : undefined;
  }

  private toCents(priceInEuros: number): number {
    return NumberUtil.isDefined(priceInEuros) ? priceInEuros * CENTS : undefined;
  }
}
