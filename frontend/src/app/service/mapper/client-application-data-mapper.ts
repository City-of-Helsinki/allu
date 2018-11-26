import {ClientApplicationData} from '../../model/application/client-application-data';
import {BackendCustomerWithContacts} from '../backend-model/backend-customer-with-contacts';
import {BackendCustomer} from '../backend-model/backend-customer';
import {CustomerMapper} from './customer-mapper';
import {CustomerWithContacts} from '@model/customer/customer-with-contacts';

export interface BackendClientApplicationData {
  customer?: BackendCustomerWithContacts;
  representative?: BackendCustomerWithContacts;
  propertyDeveloper?: BackendCustomerWithContacts;
  contractor?: BackendCustomerWithContacts;
  invoicingCustomer?: BackendCustomer;
  clientApplicationKind?: string;
}

export class ClientApplicationDataMapper {
  static mapBackend(backendData: BackendClientApplicationData): ClientApplicationData {
    if (backendData) {
      return new ClientApplicationData(
        CustomerMapper.mapBackendWithContacts(backendData.customer),
        CustomerMapper.mapBackendWithContacts(backendData.representative),
        CustomerMapper.mapBackendWithContacts(backendData.propertyDeveloper),
        CustomerMapper.mapBackendWithContacts(backendData.contractor),
        CustomerMapper.mapBackend(backendData.invoicingCustomer),
        backendData.clientApplicationKind
      );
    } else {
      return undefined;
    }
  }
}
