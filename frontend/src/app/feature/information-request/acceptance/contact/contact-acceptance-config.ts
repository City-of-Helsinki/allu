import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {MemoizedSelector} from '@ngrx/store';
import * as fromInformationRequest from '@feature/information-request/reducers';
import * as fromCustomerSearch from '@feature/customerregistry/reducers';
import {Contact} from '@model/customer/contact';
import {Customer} from '@model/customer/customer';

export interface ContactAcceptanceConfig {
  actionTargetType: ActionTargetType;
  getCustomer: MemoizedSelector<object, Customer>;
  getAvailableContacts: MemoizedSelector<object, Contact[]>;
  getMatchingContacts: MemoizedSelector<object, Contact[]>;
  getContactsLoading: MemoizedSelector<object, boolean>;
  getContactsLoaded: MemoizedSelector<object, boolean>;
}

export const config: {[key: string]: ContactAcceptanceConfig} = {
  CUSTOMER: {
    actionTargetType: ActionTargetType.Applicant,
    getCustomer: fromInformationRequest.getApplicant,
    getAvailableContacts: fromCustomerSearch.getApplicantContactsSelectors.getAvailableContacts,
    getMatchingContacts: fromCustomerSearch.getApplicantContactsSelectors.getMatchingContacts,
    getContactsLoading: fromCustomerSearch.getApplicantContactsSelectors.getContactsLoading,
    getContactsLoaded: fromCustomerSearch.getApplicantContactsSelectors.getContactsLoaded
  },
  REPRESENTATIVE: {
    actionTargetType: ActionTargetType.Representative,
    getCustomer: fromInformationRequest.getRepresentative,
    getAvailableContacts: fromCustomerSearch.getRepresentativeContactsSelectors.getAvailableContacts,
    getMatchingContacts: fromCustomerSearch.getRepresentativeContactsSelectors.getMatchingContacts,
    getContactsLoading: fromCustomerSearch.getRepresentativeContactsSelectors.getContactsLoading,
    getContactsLoaded: fromCustomerSearch.getRepresentativeContactsSelectors.getContactsLoaded
  },
  PROPERTY_DEVELOPER: {
    actionTargetType: ActionTargetType.PropertyDeveloper,
    getCustomer: fromInformationRequest.getPropertyDeveloper,
    getAvailableContacts: fromCustomerSearch.getPropertyDeveloperContactsSelectors.getAvailableContacts,
    getMatchingContacts: fromCustomerSearch.getPropertyDeveloperContactsSelectors.getMatchingContacts,
    getContactsLoading: fromCustomerSearch.getPropertyDeveloperContactsSelectors.getContactsLoading,
    getContactsLoaded: fromCustomerSearch.getPropertyDeveloperContactsSelectors.getContactsLoaded
  },
  CONTRACTOR: {
    actionTargetType: ActionTargetType.Contractor,
    getCustomer: fromInformationRequest.getContractor,
    getAvailableContacts: fromCustomerSearch.getContractorContactsSelectors.getAvailableContacts,
    getMatchingContacts: fromCustomerSearch.getContractorContactsSelectors.getMatchingContacts,
    getContactsLoading: fromCustomerSearch.getContractorContactsSelectors.getContactsLoading,
    getContactsLoaded: fromCustomerSearch.getContractorContactsSelectors.getContactsLoaded
  }
};

