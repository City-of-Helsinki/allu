import {BackendApplication} from '../backend-model/backend-application';
import {Application} from '../../model/application/application';
import {ProjectMapper} from './project-mapper';
import {CustomerMapper} from './customer-mapper';
import {LocationMapper} from './location-mapper';
import {ApplicationExtensionMapper} from './application-extension-mapper';
import {AttachmentInfoMapper} from './attachment-info-mapper';
import {UserMapper} from './user-mapper';
import {TimeUtil} from '../../util/time.util';
import {ApplicationTagMapper} from './application-tag-mapper';
import {CommentMapper} from '../application/comment/comment-mapper';
import {DistributionMapper} from './distribution-mapper';
import {StatusChangeComment} from '../../model/application/status-change-comment';
import {CommentType} from '../../model/application/comment/comment-type';

export class ApplicationMapper {

  public static mapBackend(backendApplication: BackendApplication): Application {
    let application = new Application();
    application.id = backendApplication.id;
    application.applicationId = backendApplication.applicationId;
    application.project = ProjectMapper.mapBackend(backendApplication.project);
    application.handler = UserMapper.mapBackend(backendApplication.handler);
    application.status = backendApplication.status;
    application.type = backendApplication.type;
    application.kindsWithSpecifiers = backendApplication.kindsWithSpecifiers;
    application.metadataVersion = backendApplication.metadataVersion;
    application.name = backendApplication.name;
    application.creationTime = TimeUtil.dateFromBackend(backendApplication.creationTime);
    application.startTime = TimeUtil.dateFromBackend(backendApplication.startTime);
    application.endTime = TimeUtil.dateFromBackend(backendApplication.endTime);
    application.recurringEndTime = TimeUtil.dateFromBackend(backendApplication.recurringEndTime);
    application.customersWithContacts = CustomerMapper.mapBackendCustomersWithContacts(backendApplication.customersWithContacts);
    application.locations = LocationMapper.mapBackendList(backendApplication.locations);
    application.extension = ApplicationExtensionMapper.mapBackend(backendApplication.extension);
    application.decisionTime = TimeUtil.dateFromBackend(backendApplication.decisionTime);
    application.decisionMaker = backendApplication.decisionMaker;
    application.decisionDistributionType = backendApplication.decisionDistributionType;
    application.decisionPublicityType = backendApplication.decisionPublicityType;
    application.decisionDistributionList = DistributionMapper.mapBackendList(backendApplication.decisionDistributionList);
    application.attachmentList = (backendApplication.attachmentList)
      ? backendApplication.attachmentList.map((attachment) => AttachmentInfoMapper.mapBackend(attachment))
      : undefined;
    application.calculatedPrice = backendApplication.calculatedPrice;
    application.priceOverride = backendApplication.priceOverride;
    application.priceOverrideReason = backendApplication.priceOverrideReason;
    application.applicationTags = ApplicationTagMapper.mapBackendList(backendApplication.applicationTags);
    application.comments = CommentMapper.mapBackendList(backendApplication.comments);
    application.notBillable = backendApplication.notBillable;
    application.notBillableReason = backendApplication.notBillableReason;
    application.invoiceRecipientId = backendApplication.invoiceRecipientId;
    return application;
  }

  public static mapFrontend(application: Application): BackendApplication {
    return {
      id: application.id,
      applicationId: application.applicationId,
      project: ProjectMapper.mapFrontend(application.project),
      handler: UserMapper.mapFrontend(application.handler),
      status: application.status,
      type: application.type,
      kindsWithSpecifiers: application.kindsWithSpecifiers,
      metadataVersion: application.metadataVersion,
      name: application.name,
      creationTime: TimeUtil.dateToBackend(application.creationTime),
      startTime: TimeUtil.dateToBackend(application.startTime),
      endTime: TimeUtil.dateToBackend(application.endTime),
      recurringEndTime: TimeUtil.dateToBackend(application.recurringEndTime),
      customersWithContacts: CustomerMapper.mapFrontendCustomersWithContacts(application.customersWithContacts),
      locations: LocationMapper.mapFrontendList(application.locations),
      extension: ApplicationExtensionMapper.mapFrontend(application),
      decisionTime: TimeUtil.dateToBackend(application.decisionTime),
      decisionMaker: application.decisionMaker,
      decisionDistributionType: application.decisionDistributionType,
      decisionPublicityType: application.decisionPublicityType,
      decisionDistributionList: DistributionMapper.mapFrontendList(application.decisionDistributionList),
      attachmentList: undefined, // attachmentList not mapped, because it cannot be updated in the backend through application
      calculatedPrice: application.calculatedPrice,
      priceOverride: application.priceOverride,
      priceOverrideReason: application.priceOverrideReason,
      applicationTags: ApplicationTagMapper.mapFrontendList(application.applicationTags),
      notBillable: application.notBillable,
      notBillableReason: application.notBillableReason,
      invoiceRecipientId: application.invoiceRecipientId
    };
  }

  public static mapComment(comment: StatusChangeComment) {
    return comment ? {
      type: comment.type ? CommentType[comment.type] : undefined,
      comment: comment.comment
    } : undefined;
  }
}
