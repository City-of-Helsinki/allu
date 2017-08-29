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
import {CustomerWithContacts} from '../customer/customer-with-contacts';
import {CustomerRoleType} from '../customer/customer-role-type';
import {ApplicationStatus} from './application-status';
import {ApplicationKind} from './type/application-kind';
import {ApplicationSpecifier, KindsWithSpecifiers} from './type/application-specifier';

export class Application {
  constructor(
    public id?: number,
    public applicationId?: string,
    public project?: Project,
    public handler?: User,
    public status?: string,
    public type?: string,
    public kindsWithSpecifiers?: KindsWithSpecifiers,
    public metadataVersion?: number,
    public name?: string,
    public creationTime?: Date,
    public startTime?: Date,
    public endTime?: Date,
    public recurringEndTime?: Date,
    public customersWithContacts?: Array<CustomerWithContacts>,
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
    this.customersWithContacts = customersWithContacts || [];
    this.attachmentList = attachmentList || [];
    this.applicationTags = applicationTags || [];
    this.comments = comments || [];
    this.decisionDistributionType = decisionDistributionType || DistributionType[DistributionType.EMAIL];
    this.decisionPublicityType = decisionPublicityType || PublicityType[PublicityType.PUBLIC];
    this.decisionDistributionList = decisionDistributionList || [];
    this.kindsWithSpecifiers = this.kindsWithSpecifiers || {};
  }

  /*
   * Getters and setters for supporting pickadate editing in UI.
   */
  get uiApplicationCreationTime(): string {
    return TimeUtil.getUiDateString(this.creationTime);
  }

  get uiStartTime(): string {
    return TimeUtil.getUiDateString(this.startTime);
  }

  set uiStartTime(dateString: string) {
    this.startTime = TimeUtil.getStartDateFromUi(dateString);
  }

  get uiEndTime(): string {
    return TimeUtil.getUiDateString(this.endTime);
  }

  set uiEndTime(dateString: string) {
    this.endTime = TimeUtil.getEndDateFromUi(dateString);
  }

  get recurringEndYear() {
    return TimeUtil.yearFromDate(this.recurringEndTime);
  }

  set recurringEndYear(year: number) {
    this.recurringEndTime = TimeUtil.dateWithYear(this.endTime, year);
  }

  get singleLocation(): Location {
    if (this.locations.length <= 1) {
      return ArrayUtil.first(this.locations);
    } else {
      throw new Error('Expected single location but application has ' + this.locations.length + ' locations');
    }
  }

  get firstLocation(): Location {
    return ArrayUtil.first(this.locations);
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

  get statusEnum(): ApplicationStatus {
    return ApplicationStatus[this.status];
  }

  get kinds() {
    return this.uiKinds.map(kind => ApplicationKind[kind]);
  }

  get kind() {
    if (this.kinds.length > 1) {
      throw new Error('Expected extension to contain single kind but it has ' + this.kinds.length);
    } else {
      return ArrayUtil.first(this.kinds);
    }
  }

  get uiKind() {
    return ApplicationKind[this.kind];
  }

  get uiKinds() {
    return this.kindsWithSpecifiers ? Object.keys(this.kindsWithSpecifiers) : [];
  }

  get uiSpecifiers() {
    return [].concat(this.uiKinds.map(kind => this.kindsWithSpecifiers[kind]));
  }

  get specifiers() {
    return this.uiSpecifiers.map(s => ApplicationSpecifier[s]);
  }

  get applicant(): CustomerWithContacts {
    return this.customerWithContactsByRole(CustomerRoleType.APPLICANT);
  }

  get contractor(): CustomerWithContacts {
    return this.customerWithContactsByRole(CustomerRoleType.CONTRACTOR);
  }

  get propertyDeveloper(): CustomerWithContacts {
    return this.customerWithContactsByRole(CustomerRoleType.PROPERTY_DEVELOPER);
  }

  get representative(): CustomerWithContacts {
    return this.customerWithContactsByRole(CustomerRoleType.REPRESENTATIVE);
  }

  public customerWithContactsByRole(roleType: CustomerRoleType): CustomerWithContacts {
    return Some(this.customersWithContacts.find(cwc => cwc.roleType === roleType))
      .orElse(new CustomerWithContacts(roleType));
  }

  public hasGeometry(): boolean {
    return this.geometryCount() > 0;
  }

  public geometryCount(): number {
    return this.locations.reduce((acc, cur) => acc + cur.geometryCount(), 0);
  }

  public updateDatesFromLocations(): void {
    this.startTime = TimeUtil.minimum(... this.locations.map(l => l.startTime));
    this.endTime = TimeUtil.maximum(... this.locations.map(l => l.endTime));
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

  private toEuros(priceInCents: number): number {
    return NumberUtil.toEuros(priceInCents);
  }

  private toCents(priceInEuros: number): number {
    return NumberUtil.toCents(priceInEuros);
  }

  private assertUniqueCustomerRole(roleType: CustomerRoleType): void {
    if (this.customersWithContacts.map(cwc => cwc.roleType).indexOf(roleType) >= 0) {
      throw new Error('Tried to customer with existing role type to application ' + CustomerRoleType[roleType]);
    }
  }
}
