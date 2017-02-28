import {BackendApplication} from '../backend-model/backend-application';
import {Application} from '../../model/application/application';
import {ProjectMapper} from './project-mapper';
import {ApplicantMapper} from './applicant-mapper';
import {ContactMapper} from './contact-mapper';
import {LocationMapper} from './location-mapper';
import {ApplicationTypeDataMapper} from './application-type-data-mapper';
import {StructureMetaMapper} from './structure-meta-mapper';
import {AttachmentInfoMapper} from './attachment-info-mapper';
import {UserMapper} from './user-mapper';
import {TimeUtil} from '../../util/time.util';
import {Some} from '../../util/option';
import {ApplicationTagMapper} from './application-tag-mapper';
import {CommentMapper} from '../application/comment/comment-mapper';

export class ApplicationMapper {

  public static mapBackend(backendApplication: BackendApplication): Application {
    return new Application(
      backendApplication.id,
      backendApplication.applicationId,
      ProjectMapper.mapBackend(backendApplication.project),
      UserMapper.mapBackend(backendApplication.handler),
      backendApplication.status,
      backendApplication.type,
      backendApplication.kind,
      backendApplication.metadataVersion,
      backendApplication.name,
      TimeUtil.dateFromBackend(backendApplication.creationTime),
      TimeUtil.dateFromBackend(backendApplication.startTime),
      TimeUtil.dateFromBackend(backendApplication.endTime),
      ApplicantMapper.mapBackend(backendApplication.applicant),
      (backendApplication.contactList) ? backendApplication.contactList.map((contact) => ContactMapper.mapBackend(contact)) : undefined,
      LocationMapper.mapBackend(backendApplication.locations[0]),
      ApplicationTypeDataMapper.mapBackend(backendApplication.extension),
      TimeUtil.dateFromBackend(backendApplication.decisionTime),
      (backendApplication.attachmentList) ? backendApplication.attachmentList.map(
        (attachment) => AttachmentInfoMapper.mapBackend(attachment)) : undefined,
      backendApplication.calculatedPrice,
      backendApplication.priceOverride,
      backendApplication.priceOverrideReason,
      ApplicationTagMapper.mapBackendList(backendApplication.applicationTags),
      CommentMapper.mapBackendList(backendApplication.comments)
    );
  }

  public static mapFrontend(application: Application): BackendApplication {
    return {
      id: application.id,
      applicationId: application.applicationId,
      project: ProjectMapper.mapFrontend(application.project),
      handler: UserMapper.mapFrontend(application.handler),
      status: application.status,
      type: application.type,
      kind: application.kind,
      metadataVersion: application.metadataVersion,
      name: application.name,
      creationTime: (application.creationTime) ? application.creationTime.toISOString() : undefined,
      startTime: application.startTime.toISOString(),
      endTime: application.endTime.toISOString(),
      applicant: ApplicantMapper.mapFrontend(application.applicant),
      contactList: (application.contactList) ? application.contactList.map((contact) => ContactMapper.mapFrontend(contact)) : undefined,
      locations: (application.location) ? [LocationMapper.mapFrontend(application.location)] : undefined,
      extension: ApplicationTypeDataMapper.mapFrontend(application.extension),
      decisionTime: Some(application.decisionTime).map(decisionTime => decisionTime.toISOString()).orElse(undefined),
      attachmentList: undefined, // attachmentList not mapped, because it cannot be updated in the backend through application
      calculatedPrice: application.calculatedPrice,
      priceOverride: application.priceOverride,
      priceOverrideReason: application.priceOverrideReason,
      applicationTags: ApplicationTagMapper.mapFrontendList(application.applicationTags)
    };
  }

  public static mapComment(comment: string) {
    return {
      comment: comment
    };
  }
}
