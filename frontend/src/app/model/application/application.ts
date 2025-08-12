import {Location} from '../common/location';
import {ApplicationExtension} from './type/application-extension';
import {AttachmentInfo} from './attachment/attachment-info';
import {User} from '../user/user';
import {Some} from '@util/option';
import {Project} from '../project/project';
import {ApplicationTag} from './tag/application-tag';
import {Comment} from './comment/comment';
import {ApplicationType} from './type/application-type';
import {PublicityType} from './publicity-type';
import {DistributionEntry} from '../common/distribution-entry';
import {ArrayUtil} from '@util/array-util';
import {CustomerWithContacts} from '../customer/customer-with-contacts';
import {CustomerRoleType} from '../customer/customer-role-type';
import {ApplicationStatus} from './application-status';
import {ApplicationKind} from './type/application-kind';
import {KindsWithSpecifiers} from './type/application-specifier';
import {ClientApplicationData} from './client-application-data';
import {InvoicingPeriodLength} from '@feature/application/invoicing/invoicing-period/invoicing-period-length';

export class Application {
  constructor(
    public id?: number,
    public applicationId?: string,
    public project?: Project,
    public owner?: User,
    public handler?: User,
    public status?: ApplicationStatus,
    public type?: ApplicationType,
    public kindsWithSpecifiers?: KindsWithSpecifiers,
    public metadataVersion?: number,
    public name?: string,
    public creationTime?: Date,
    public receivedTime?: Date,
    public startTime?: Date,
    public endTime?: Date,
    public recurringEndTime?: Date,
    public customersWithContacts?: Array<CustomerWithContacts>,
    public locations?: Array<Location>,
    public extension?: ApplicationExtension,
    public decisionTime?: Date,
    public decisionMaker?: string,
    public decisionPublicityType?: string,
    public decisionDistributionList?: Array<DistributionEntry>,
    public attachmentList?: Array<AttachmentInfo>,
    public calculatedPrice?: number,
    public applicationTags?: Array<ApplicationTag>,
    public comments?: Array<Comment>,
    public notBillable: boolean = false,
    public notBillableReason?: string,
    public invoiceRecipientId?: number,
    public replacesApplicationId?: number,
    public replacedByApplicationId?: number,
    public customerReference?: string,
    public invoicingDate?: Date,
    public identificationNumber?: string,
    public skipPriceCalculation: boolean = false,
    public clientApplicationData?: ClientApplicationData,
    public externalOwnerId?: number,
    public nrOfComments?: number,
    public latestComment?: string,
    public ownerNotification: boolean = false,
    public invoiced?: boolean,
    public invoicingChanged: boolean = false,
    public targetState?: ApplicationStatus,
    public invoicingPeriodLength?: InvoicingPeriodLength,
    public terminationTime?: Date,
    public version?: number) {
    this.locations = locations || [];
    this.customersWithContacts = customersWithContacts || [];
    this.attachmentList = attachmentList || [];
    this.applicationTags = applicationTags || [];
    this.comments = comments || [];
    this.decisionPublicityType = decisionPublicityType || PublicityType[PublicityType.PUBLIC];
    this.decisionDistributionList = decisionDistributionList || [];
    this.kindsWithSpecifiers = this.kindsWithSpecifiers || {};
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

  get address() {
    const addresses = new Set();
    this.locations.forEach(l => addresses.add(l.address));
    return Array.from(addresses).join(', ');
  }

  get kinds() {
    return this.uiKinds.map(k => ApplicationKind[k]);
  }

  get kind() {
    if (this.kinds.length > 1) {
      throw new Error('Expected extension to contain single kind but it has ' + this.kinds.length);
    } else {
      return ArrayUtil.first(this.kinds);
    }
  }

  get uiKinds() {
    return this.kindsWithSpecifiers ? Object.keys(this.kindsWithSpecifiers) : [];
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
}

export function hasFixedLocations(application: Application) {
  return Some(application)
    .map(app => app.locations)
    .map(locations => locations.some(location => location.hasFixedGeometry()))
    .orElse(false);
}
