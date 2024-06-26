import {BackendProject, SearchResultProject} from './backend-project';
import {BackendLocation, SearchResultLocation} from './backend-location';
import {BackendAttachmentInfo} from './backend-attachment-info';
import {BackendUser, SearchResultUser} from './backend-user';
import {BackendApplicationTag} from '../mapper/application-tag-mapper';
import {BackendComment} from '../application/comment/comment-mapper';
import {BackendDistributionEntry} from './backend-distribution-entry';
import {BackendCustomerWithContacts, SearchResultCustomersWithContacts} from './backend-customer-with-contacts';
import {KindsWithSpecifiers} from '../../model/application/type/application-specifier';
import {BackendClientApplicationData} from '../mapper/client-application-data-mapper';
import {SearchResultEnumType} from './search-result-enum-type';
import {ApplicationType} from '@model/application/type/application-type';
import {ApplicationStatus} from '@model/application/application-status';
import {ApplicationTagType} from '@model/application/tag/application-tag-type';
import {InvoicingPeriodLength} from '@feature/application/invoicing/invoicing-period/invoicing-period-length';

export interface BackendApplication {
  id: number;
  applicationId: string;
  project: BackendProject;
  owner: BackendUser;
  handler: BackendUser;
  status: ApplicationStatus;
  type: ApplicationType;
  kindsWithSpecifiers?: KindsWithSpecifiers;
  metadataVersion: number;
  name: string;
  creationTime: string;
  receivedTime: string;
  startTime: string;
  endTime: string;
  recurringEndTime: string;
  customersWithContacts: Array<BackendCustomerWithContacts>;
  locations: Array<BackendLocation>;
  extension: any;
  decisionTime: string;
  decisionMaker: string;
  decisionPublicityType?: string;
  decisionDistributionList?: Array<BackendDistributionEntry>;
  attachmentList: Array<BackendAttachmentInfo>;
  calculatedPrice: number;
  applicationTags: Array<BackendApplicationTag>;
  comments?: Array<BackendComment>;
  notBillable: boolean;
  notBillableReason?: string;
  invoiceRecipientId?: number;
  replacesApplicationId?: number;
  replacedByApplicationId?: number;
  customerReference?: string;
  invoicingDate?: Date;
  identificationNumber?: string;
  skipPriceCalculation: boolean;
  clientApplicationData?: BackendClientApplicationData;
  externalOwnerId?: number;
  invoiced?: boolean;
  invoicingChanged?: boolean;
  targetState?: ApplicationStatus;
  invoicingPeriodLength?: InvoicingPeriodLength;
  version: number;
  ownerNotification?: boolean;
  terminationTime?: string;
}

export interface SearchResultApplication {
  id: number;
  applicationId: string;
  project: SearchResultProject;
  owner: SearchResultUser;
  status: SearchResultEnumType;
  type: SearchResultEnumType;
  name: string;
  creationTime: string;
  receivedTime: string;
  startTime: string;
  endTime: string;
  customers: SearchResultCustomersWithContacts;
  locations: Array<SearchResultLocation>;
  nrOfComments: number;
  latestComment: string;
  applicationTags: ApplicationTagType[];
  ownerNotification: boolean;
  recurringApplication?: RecurringApplication;
  terminationTime?: string;
}

export interface RecurringApplication {
  startTime: string;
  endTime: string;
  period1Start: string;
  period1End: string;
  period2Start: string;
  period2End: string;
}
