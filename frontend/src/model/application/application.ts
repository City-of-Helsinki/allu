import {Applicant} from './applicant';
import {Contact} from './contact';
import {Location} from '../common/location';
import {ApplicationExtension} from './type/application-extension';
import {AttachmentInfo} from './attachment/attachment-info';
import {TimeUtil} from '../../util/time.util';
import {User} from '../common/user';
import {Some} from '../../util/option';
import {Project} from '../project/project';
import {ApplicationTag} from './tag/application-tag';
import {ApplicationTagType} from './tag/application-tag-type';
import {Comment} from './comment/comment';
import {NumberUtil} from '../../util/number.util';
import {ApplicationType} from './type/application-type';
import {PublicityType} from './publicity-type';
import {DistributionEntry} from '../common/distribution-entry';
import {DistributionType} from '../common/distribution-type';
import {ArrayUtil} from '../../util/array-util';

const CENTS = 100;

export class Application {
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
    public locations?: Array<Location>,
    public extension?: ApplicationExtension,
    public decisionTime?: Date,
    public decisionMaker?: string,
    public decisionDistributionType?: string,
    public decisionPublicityType?: string,
    public decisionDistributionList?: Array<DistributionEntry>,
    public attachmentList?: Array<AttachmentInfo>,
    public calculatedPrice?: number,
    public priceOverride?: number,
    public priceOverrideReason?: string,
    public applicationTags?: Array<ApplicationTag>,
    public comments?: Array<Comment>) {
    this.locations = locations || [];
    this.contactList = contactList || [new Contact()];
    this.attachmentList = attachmentList || [];
    this.applicationTags = applicationTags || [];
    this.comments = comments || [];
    this.decisionDistributionType = decisionDistributionType || DistributionType[DistributionType.EMAIL];
    this.decisionPublicityType = decisionPublicityType || PublicityType[PublicityType.PUBLIC];
    this.decisionDistributionList = decisionDistributionList || [];
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

  public updateDatesFromLocations(): void {
    this.startTime = TimeUtil.minimum(... this.locations.map(l => l.startTime));
    this.endTime = TimeUtil.maximum(... this.locations.map(l => l.endTime));
  }

  public get singleLocation(): Location {
    if (this.locations.length <= 1) {
      return ArrayUtil.first(this.locations);
    } else {
      throw new Error('Expected single location but application has ' + this.locations.length + ' locations');
    }
  }

  public set singleLocation(location: Location) {
    this.locations = [location];
  }

  public get firstLocation(): Location {
    return ArrayUtil.first(this.locations);
  }

  public hasGeometry(): boolean {
    return this.geometryCount() > 0;
  }

  public geometryCount(): number {
    return this.locations.reduce((acc, cur) => acc + cur.geometryCount(), 0);
  }

  public geometries(): Array<GeoJSON.GeometryCollection> {
    return this.locations.map(l => l.geometry);
  }

  public hasFixedGeometry(): boolean {
    return this.locations.some(l => l.hasFixedGeometry());
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

  get typeEnum(): ApplicationType {
    return ApplicationType[this.type];
  }

  private toEuros(priceInCents: number): number {
    return NumberUtil.isDefined(priceInCents) ? priceInCents / CENTS : undefined;
  }

  private toCents(priceInEuros: number): number {
    return NumberUtil.isDefined(priceInEuros) ? priceInEuros * CENTS : undefined;
  }
}
