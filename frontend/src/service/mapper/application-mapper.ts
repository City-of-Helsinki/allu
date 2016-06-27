import {BackendApplication} from '../backend-model/backend-application';
import {Application} from '../../model/application/application';
import {CustomerMapper} from './customer-mapper';
import {ProjectMapper} from './project-mapper';
import {ApplicantMapper} from './applicant-mapper';
import {ContactMapper} from './contact-mapper';
import {LocationMapper} from './location-mapper';
import {ApplicationTypeDataMapper} from './application-type-data-mapper';
import {BillingDetailMapper} from './billing-detail-mapper';
import {SalesMapper} from './sales-mapper';
import {PricingMapper} from './pricing-mapper';
import {StructureMapper} from './structure-mapper';

export class ApplicationMapper {

  public static mapBackend(backendApplication: BackendApplication): Application {
    return new Application(
      backendApplication.id,
      ProjectMapper.mapBackend(backendApplication.project),
      backendApplication.handler,
      CustomerMapper.mapBackend(backendApplication.customer),
      backendApplication.status,
      backendApplication.type,
      backendApplication.name,
      BillingDetailMapper.mapBackend(backendApplication.billingDetail),
      SalesMapper.mapBackend(backendApplication.sales),
      ApplicationTypeDataMapper.mapBackend(backendApplication.event),
      PricingMapper.mapBackend(backendApplication.pricing),
      StructureMapper.mapBackend(backendApplication.structure),
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
      customer: CustomerMapper.mapFrontend(application.customer),
      status: application.status,
      type: application.type,
      name: application.name,
      billingDetail: BillingDetailMapper.mapFrontend(application.billingDetail),
      sales: SalesMapper.mapFrontend(application.sales),
      event: ApplicationTypeDataMapper.mapFrontend(application.event),
      pricing: PricingMapper.mapFrontend(application.pricing),
      structure: StructureMapper.mapFrontend(application.structure),
      creationTime: (application.creationTime) ? application.creationTime.toISOString() : undefined,
      applicant: ApplicantMapper.mapFrontend(application.applicant),
      contactList: (application.contactList) ? application.contactList.map((contact) => ContactMapper.mapFrontend(contact)) : undefined,
      location: LocationMapper.mapFrontend(application.location),
      comments : application.comments
    };
  }
}
