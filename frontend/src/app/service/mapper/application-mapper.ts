import {BackendApplication, SearchResultApplication} from '../backend-model/backend-application';
import {Application} from '@model/application/application';
import {ProjectMapper} from './project-mapper';
import {CustomerMapper} from './customer-mapper';
import {LocationMapper} from './location-mapper';
import {ApplicationExtensionMapper} from './application-extension-mapper';
import {AttachmentInfoMapper} from './attachment-info-mapper';
import {UserMapper} from './user-mapper';
import {TimeUtil} from '@util/time.util';
import {ApplicationTagMapper} from './application-tag-mapper';
import {CommentMapper} from '../application/comment/comment-mapper';
import {DistributionMapper} from './distribution-mapper';
import {Some} from '@util/option';
import {ClientApplicationDataMapper} from './client-application-data-mapper';
import {ApplicationType} from '@model/application/type/application-type';
import {ApplicationStatus} from '@model/application/application-status';

export class ApplicationMapper {

  public static mapBackendList(backendApplications: BackendApplication[]): Application[] {
    return backendApplications
      ? backendApplications.map(app => ApplicationMapper.mapBackend(app))
      : [];
  }

  public static mapSearchResult(backendApplication: SearchResultApplication): Application {
    const application = new Application();
    application.id = backendApplication.id;
    application.applicationId = backendApplication.applicationId;
    application.name = backendApplication.name;
    application.type = <ApplicationType>backendApplication.type.value;
    application.status = <ApplicationStatus>backendApplication.status.value;
    application.owner = UserMapper.mapSearchResult(backendApplication.owner);
    application.locations = LocationMapper.mapSearchResultList(backendApplication.locations);
    application.customersWithContacts = CustomerMapper.mapSearchResultsWithContacts(backendApplication.customers);
    application.project = ProjectMapper.mapSearchResult(backendApplication.project);
    application.startTime = TimeUtil.dateFromBackend(backendApplication.startTime);
    application.endTime = TimeUtil.dateFromBackend(backendApplication.endTime);
    application.creationTime = TimeUtil.dateFromBackend(backendApplication.creationTime);
    application.receivedTime = TimeUtil.dateFromBackend(backendApplication.receivedTime);
    application.nrOfComments = backendApplication.nrOfComments;
    application.applicationTags = ApplicationTagMapper.mapSearchResultList(backendApplication.applicationTags);
    application.ownerNotification = backendApplication.ownerNotification;
    application.recurringEndTime = getRecurringEndDate(backendApplication);
    return application;
  }

  public static mapCommon(backendApplication: BackendApplication): Application {
    const application = new Application();
    application.id = backendApplication.id;
    application.applicationId = backendApplication.applicationId;
    application.name = backendApplication.name;
    application.type = backendApplication.type;
    application.startTime = TimeUtil.dateFromBackend(backendApplication.startTime);
    application.endTime = TimeUtil.dateFromBackend(backendApplication.endTime);
    application.project = ProjectMapper.mapBackend(backendApplication.project);
    application.locations = LocationMapper.mapBackendList(backendApplication.locations);
    application.status = backendApplication.status;
    application.receivedTime = TimeUtil.dateFromBackend(backendApplication.receivedTime);
    return application;
  }

  public static mapBackend(backendApplication: BackendApplication): Application {
    const application = ApplicationMapper.mapCommon(backendApplication);
    application.owner = UserMapper.mapBackend(backendApplication.owner);
    application.handler = UserMapper.mapBackend(backendApplication.handler);
    application.status = backendApplication.status;
    application.kindsWithSpecifiers = backendApplication.kindsWithSpecifiers;
    application.metadataVersion = backendApplication.metadataVersion;
    application.creationTime = TimeUtil.dateFromBackend(backendApplication.creationTime);
    application.receivedTime = TimeUtil.dateFromBackend(backendApplication.receivedTime);
    application.recurringEndTime = TimeUtil.dateFromBackend(backendApplication.recurringEndTime);
    application.customersWithContacts = CustomerMapper.mapBackendCustomersWithContacts(backendApplication.customersWithContacts);
    application.extension = ApplicationExtensionMapper.mapBackend(backendApplication.extension);
    application.decisionTime = TimeUtil.dateFromBackend(backendApplication.decisionTime);
    application.decisionMaker = backendApplication.decisionMaker;
    application.decisionPublicityType = backendApplication.decisionPublicityType;
    application.decisionDistributionList = DistributionMapper.mapBackendList(backendApplication.decisionDistributionList);
    application.attachmentList = Some(backendApplication.attachmentList)
      .map(attachments => attachments.map((attachment) => AttachmentInfoMapper.mapBackend(attachment)))
      .orElse(undefined);
    application.calculatedPrice = backendApplication.calculatedPrice;
    application.applicationTags = ApplicationTagMapper.mapBackendList(backendApplication.applicationTags);
    application.comments = CommentMapper.mapBackendList(backendApplication.comments);
    application.notBillable = backendApplication.notBillable;
    application.notBillableReason = backendApplication.notBillableReason;
    application.invoiceRecipientId = backendApplication.invoiceRecipientId;
    application.replacesApplicationId = backendApplication.replacesApplicationId;
    application.replacedByApplicationId = backendApplication.replacedByApplicationId;
    application.customerReference = backendApplication.customerReference;
    application.invoicingDate = backendApplication.invoicingDate;
    application.identificationNumber = backendApplication.identificationNumber;
    application.skipPriceCalculation = backendApplication.skipPriceCalculation;
    application.invoiced = backendApplication.invoiced;
    application.clientApplicationData = ClientApplicationDataMapper.mapBackend(backendApplication.clientApplicationData);
    application.externalOwnerId = backendApplication.externalOwnerId;
    application.invoicingChanged = backendApplication.invoicingChanged;
    application.targetState = backendApplication.targetState;
    application.invoicingPeriodLength = backendApplication.invoicingPeriodLength;
    application.version = backendApplication.version;
    application.ownerNotification = backendApplication.ownerNotification;
    return application;
  }

  public static mapFrontend(application: Application): BackendApplication {
    return {
      id: application.id,
      applicationId: application.applicationId,
      project: ProjectMapper.mapFrontend(application.project),
      owner: UserMapper.mapFrontend(application.owner),
      handler: UserMapper.mapFrontend(application.handler),
      status: application.status,
      type: application.type,
      kindsWithSpecifiers: application.kindsWithSpecifiers,
      metadataVersion: application.metadataVersion,
      name: application.name,
      creationTime: TimeUtil.dateToBackend(application.creationTime),
      receivedTime: TimeUtil.dateToBackend(application.receivedTime),
      startTime: TimeUtil.dateToBackend(application.startTime),
      endTime: TimeUtil.dateToBackend(application.endTime),
      recurringEndTime: TimeUtil.dateToBackend(application.recurringEndTime),
      customersWithContacts: CustomerMapper.mapFrontendCustomersWithContacts(application.customersWithContacts),
      locations: LocationMapper.mapFrontendList(application.locations),
      extension: ApplicationExtensionMapper.mapFrontend(application),
      decisionTime: TimeUtil.dateToBackend(application.decisionTime),
      decisionMaker: application.decisionMaker,
      decisionPublicityType: application.decisionPublicityType,
      decisionDistributionList: DistributionMapper.mapFrontendList(application.decisionDistributionList),
      attachmentList: undefined, // attachmentList not mapped, because it cannot be updated in the backend through application
      calculatedPrice: application.calculatedPrice,
      applicationTags: ApplicationTagMapper.mapFrontendList(application.applicationTags),
      notBillable: application.notBillable,
      notBillableReason: application.notBillableReason,
      invoiceRecipientId: application.invoiceRecipientId,
      replacedByApplicationId: application.replacedByApplicationId,
      replacesApplicationId: application.replacesApplicationId,
      customerReference: application.customerReference,
      invoicingDate: application.invoicingDate,
      identificationNumber: application.identificationNumber,
      skipPriceCalculation: application.skipPriceCalculation,
      version: application.version
    };
  }
}

export const getRecurringEndDate = (searchResult: SearchResultApplication) => {
  const appEndTime = TimeUtil.dateFromBackend(searchResult.endTime);
  const recurringEndTime = searchResult.recurringApplication
    ? TimeUtil.dateFromBackend(searchResult.recurringApplication.endTime)
    : undefined;

  if (TimeUtil.isAfter(recurringEndTime, appEndTime)) {
    return recurringEndTime;
  } else {
    return undefined;
  }
};
