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
    getAvailableContacts: fromCustomerSearch.getApplicantContactsSelectors.getAvailable,
    getMatchingContacts: fromCustomerSearch.getApplicantContactsSelectors.getMatching,
    getContactsLoading: fromCustomerSearch.getApplicantContactsSelectors.getLoading,
    getContactsLoaded: fromCustomerSearch.getApplicantContactsSelectors.getLoaded
  },
  REPRESENTATIVE: {
    actionTargetType: ActionTargetType.Representative,
    getCustomer: fromInformationRequest.getRepresentative,
    getAvailableContacts: fromCustomerSearch.getRepresentativeContactsSelectors.getAvailable,
    getMatchingContacts: fromCustomerSearch.getRepresentativeContactsSelectors.getMatching,
    getContactsLoading: fromCustomerSearch.getRepresentativeContactsSelectors.getLoading,
    getContactsLoaded: fromCustomerSearch.getRepresentativeContactsSelectors.getLoaded
  },
  PROPERTY_DEVELOPER: {
    actionTargetType: ActionTargetType.PropertyDeveloper,
    getCustomer: fromInformationRequest.getPropertyDeveloper,
    getAvailableContacts: fromCustomerSearch.getPropertyDeveloperContactsSelectors.getAvailable,
    getMatchingContacts: fromCustomerSearch.getPropertyDeveloperContactsSelectors.getMatching,
    getContactsLoading: fromCustomerSearch.getPropertyDeveloperContactsSelectors.getLoading,
    getContactsLoaded: fromCustomerSearch.getPropertyDeveloperContactsSelectors.getLoaded
  },
  CONTRACTOR: {
    actionTargetType: ActionTargetType.Contractor,
    getCustomer: fromInformationRequest.getContractor,
    getAvailableContacts: fromCustomerSearch.getContractorContactsSelectors.getAvailable,
    getMatchingContacts: fromCustomerSearch.getContractorContactsSelectors.getMatching,
    getContactsLoading: fromCustomerSearch.getContractorContactsSelectors.getLoading,
    getContactsLoaded: fromCustomerSearch.getContractorContactsSelectors.getLoaded
  }
};

