import {BackendApplication} from '../backend-model/backend-application';
import {Application} from '../../model/application/application';
import {CustomerMapper} from './customer-mapper';

export class ApplicationMapper {

  public static mapBackend(backendApplication: BackendApplication): Application {
    return new Application(
      backendApplication.id,
      undefined,
      backendApplication.type,
      backendApplication.status,
      undefined,
      undefined,
      undefined,
      CustomerMapper.mapBackend(backendApplication.customer),
      undefined,
      undefined,
      undefined,
      undefined);
  }

  public static mapFrontend(application: Application): BackendApplication {
    return {
      id: application.id,
      name: application.title,
      type: application.type,
      status: application.status,
      handler: undefined, // TODO: missing from application
      information: undefined, // TODO: what's information?
      createDate: undefined, // TODO: missing from application
      startDate: undefined, // TODO: missing from application
      customer: CustomerMapper.mapFrontend(application.customer),
      project: undefined // TODO: add mapping
    };
  }
}
