import {BackendCustomer, SearchResultCustomer} from '../backend-model/backend-customer';
import {PostalAddress} from '../../model/common/postal-address';
import {BackendCustomerWithContacts, SearchResultCustomersWithContacts,
SearchResultCustomerWithContacts} from '../backend-model/backend-customer-with-contacts';
import {ContactMapper} from './contact-mapper';
import {Customer} from '../../model/customer/customer';
import {CustomerWithContacts} from '../../model/customer/customer-with-contacts';
import {CustomerRoleType} from '../../model/customer/customer-role-type';

export class CustomerMapper {

  public static mapBackend(backendCustomer: BackendCustomer): Customer {
    if (backendCustomer) {
      let postalAddress;
      if (backendCustomer.postalAddress) {
        postalAddress = new PostalAddress(
          backendCustomer.postalAddress.streetAddress, backendCustomer.postalAddress.postalCode, backendCustomer.postalAddress.city);
      }

      return new Customer(
        backendCustomer.id,
        backendCustomer.type,
        backendCustomer.name,
        backendCustomer.registryKey,
        backendCustomer.ovt,
        backendCustomer.invoicingOperator,
        postalAddress,
        backendCustomer.email,
        backendCustomer.phone,
        backendCustomer.country,
        backendCustomer.active,
        backendCustomer.sapCustomerNumber,
        backendCustomer.invoicingProhibited,
        backendCustomer.invoicingOnly,
        backendCustomer.projectIdentifierPrefix);
    } else {
      return undefined;
    }
  }

  public static mapSearchResultCustomer(backendCustomer: SearchResultCustomer): Customer {
    if (backendCustomer) {
      const customer = new Customer();
      customer.name = backendCustomer.name;
      customer.id = backendCustomer.id;
      return customer;
    } else {
      return undefined;
    }
  }

  public static mapFrontend(customer: Customer): BackendCustomer {
    return (customer) ?
    {
      id: customer.id,
      type: customer.type,
      name: customer.name,
      registryKey: customer.registryKey,
      ovt: customer.ovt,
      invoicingOperator: customer.invoicingOperator,
      postalAddress: (customer.postalAddress) ?
        { streetAddress: customer.postalAddress.streetAddress,
          postalCode: customer.postalAddress.postalCode,
          city: customer.postalAddress.city } : undefined,
      email: customer.email,
      phone: customer.phone,
      active: customer.active,
      invoicingOnly: customer.invoicingOnly,
      country: customer.country,
      projectIdentifierPrefix: customer.projectIdentifierPrefix
    } : undefined;

  }

  public static mapBackendWithContacts(customer: BackendCustomerWithContacts): CustomerWithContacts {
    return new CustomerWithContacts(
      CustomerRoleType[customer.roleType],
      CustomerMapper.mapBackend(customer.customer),
      customer.contacts.map(contact => ContactMapper.mapBackend(contact))
    );
  }

  public static mapFrontendWithContacts(customer: CustomerWithContacts): BackendCustomerWithContacts {
    return {
      roleType: CustomerRoleType[customer.roleType],
      customer: customer.customer ? CustomerMapper.mapFrontend(customer.customer) : undefined,
      contacts: customer.contacts ? customer.contacts.map(contact => ContactMapper.mapFrontend(contact)) : []
    };
  }

  public static mapSearchResultApplicantWithContacts(customersWithContacts: SearchResultCustomerWithContacts): CustomerWithContacts {
    return new CustomerWithContacts(
      CustomerRoleType.APPLICANT,
      CustomerMapper.mapSearchResultCustomer(customersWithContacts.customer)
    );
  }
  public static mapSearchResultsWithContacts(customersWithContacts: SearchResultCustomersWithContacts): Array<CustomerWithContacts> {
    return customersWithContacts ? [this.mapSearchResultApplicantWithContacts(customersWithContacts.applicant)] : [];
  }

  public static mapBackendCustomersWithContacts(customersWithContacts: Array<BackendCustomerWithContacts>): Array<CustomerWithContacts> {
    return customersWithContacts ? customersWithContacts.map(cwc => CustomerMapper.mapBackendWithContacts(cwc)) : [];
  }

  public static mapFrontendCustomersWithContacts(customersWithContacts: Array<CustomerWithContacts>): Array<BackendCustomerWithContacts> {
    return customersWithContacts ? customersWithContacts.map(cwc => CustomerMapper.mapFrontendWithContacts(cwc)) : [];
  }
}
