import {BackendApplication} from '../backend-model/backend-application';
import {Application} from '../../model/application/application';
import {ProjectMapper} from './project-mapper';
import {ApplicantMapper} from './applicant-mapper';
import {ContactMapper} from './contact-mapper';
import {LocationMapper} from './location-mapper';
import {ApplicationTypeDataMapper} from './application-type-data-mapper';
import {BillingDetailMapper} from './billing-detail-mapper';
import {StructureMetaMapper} from './structure-meta-mapper';

export class ApplicationMapper {

  public static mapBackend(backendApplication: BackendApplication): Application {
    return new Application(
      backendApplication.id,
      ProjectMapper.mapBackend(backendApplication.project),
      backendApplication.handler,
      backendApplication.status,
      backendApplication.type,
      backendApplication.name,
      BillingDetailMapper.mapBackend(backendApplication.billingDetail),
      ApplicationTypeDataMapper.mapBackend(backendApplication.event),
      StructureMetaMapper.mapBackend(backendApplication.metadata),
      new Date(backendApplication.creationTime),
      ApplicantMapper.mapBackend(backendApplication.applicant),
      (backendApplication.contactList) ? backendApplication.contactList.map((contact) => ContactMapper.mapBackend(contact)) : undefined,
      LocationMapper.mapBackend(backendApplication.location),
      backendApplication.comments
    );
  }

  public static mapFrontend(application: Application): BackendApplication {
    return {
      id: application.id,
      project: ProjectMapper.mapFrontend(application.project),
      handler: application.handler,
      status: application.status,
      type: application.type,
      name: application.name,
      billingDetail: BillingDetailMapper.mapFrontend(application.billingDetail),
      event: ApplicationTypeDataMapper.mapFrontend(application.event),
      metadata: StructureMetaMapper.mapFrontend(application.metadata),
      creationTime: (application.creationTime) ? application.creationTime.toISOString() : undefined,
      applicant: ApplicantMapper.mapFrontend(application.applicant),
      contactList: (application.contactList) ? application.contactList.map((contact) => ContactMapper.mapFrontend(contact)) : undefined,
      location: LocationMapper.mapFrontend(application.location),
      comments : application.comments
    };
  }
}
