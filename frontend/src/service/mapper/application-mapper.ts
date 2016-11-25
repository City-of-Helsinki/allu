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

export class ApplicationMapper {

  public static mapBackend(backendApplication: BackendApplication): Application {
    return new Application(
      backendApplication.id,
      backendApplication.applicationId,
      ProjectMapper.mapBackend(backendApplication.project),
      UserMapper.mapBackend(backendApplication.handler),
      backendApplication.status,
      backendApplication.type,
      backendApplication.specifiers,
      backendApplication.name,
      ApplicationTypeDataMapper.mapBackend(backendApplication.event),
      StructureMetaMapper.mapBackend(backendApplication.metadata),
      new Date(backendApplication.creationTime),
      new Date(backendApplication.startTime),
      new Date(backendApplication.endTime),
      ApplicantMapper.mapBackend(backendApplication.applicant),
      (backendApplication.contactList) ? backendApplication.contactList.map((contact) => ContactMapper.mapBackend(contact)) : undefined,
      LocationMapper.mapBackend(backendApplication.location),
      backendApplication.calculatedPrice,
      backendApplication.priceOverride,
      backendApplication.priceOverrideReason,
      (backendApplication.attachmentList) ? backendApplication.attachmentList.map(
            (attachment) => AttachmentInfoMapper.mapBackend(attachment)) : undefined
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
      specifiers: application.specifiers,
      name: application.name,
      event: ApplicationTypeDataMapper.mapFrontend(application.event),
      metadata: StructureMetaMapper.mapFrontend(application.metadata),
      creationTime: (application.creationTime) ? application.creationTime.toISOString() : undefined,
      startTime: application.startTime.toISOString(),
      endTime: application.endTime.toISOString(),
      applicant: ApplicantMapper.mapFrontend(application.applicant),
      contactList: (application.contactList) ? application.contactList.map((contact) => ContactMapper.mapFrontend(contact)) : undefined,
      location: LocationMapper.mapFrontend(application.location),
      calculatedPrice: application.calculatedPrice,
      priceOverride: application.priceOverride,
      priceOverrideReason: application.priceOverrideReason,
      attachmentList: undefined // attachmentList not mapped, because it cannot be updated in the backend through application
    };
  }

  public static mapComment(comment: string) {
    return {
      comment: comment
    };
  }
}
