import {ClientApplicationData} from '../../model/application/client-application-data';
import {BackendCustomerWithContacts} from '../backend-model/backend-customer-with-contacts';
import {BackendCustomer} from '../backend-model/backend-customer';
import {CustomerMapper} from './customer-mapper';

export interface BackendClientApplicationData {
  customer?: BackendCustomerWithContacts;
  invoicingCustomer?: BackendCustomer;
  clientApplicationKind?: string;
}

export class ClientApplicationDataMapper {
  static mapBackend(backendData: BackendClientApplicationData): ClientApplicationData {
    if (backendData) {
      return new ClientApplicationData(
        CustomerMapper.mapBackendWithContacts(backendData.customer),
        CustomerMapper.mapBackend(backendData.invoicingCustomer),
        backendData.clientApplicationKind
      );
    } else {
      return undefined;
    }
  }
}
