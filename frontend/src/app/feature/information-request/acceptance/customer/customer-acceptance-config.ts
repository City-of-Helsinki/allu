import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Action, MemoizedSelector} from '@ngrx/store';
import {Customer} from '@model/customer/customer';
import * as fromCustomerSearch from '@feature/customerregistry/reducers';
import {CustomerRoleType} from '@model/customer/customer-role-type';
import {SetCustomer} from '@feature/information-request/actions/information-request-result-actions';

export interface CustomerAcceptanceConfig {
  formName: string;
  roleType: CustomerRoleType;
  actionTargetType: ActionTargetType;
  matchingCustomersSelector: MemoizedSelector<object, Customer[]>;
  customersLoadingSelector: MemoizedSelector<object, boolean>;
  saveAction: (customer: Customer) => Action;
}

export const config: {[key: string]: CustomerAcceptanceConfig} = {
  CUSTOMER: {
    formName: 'applicant',
    roleType: CustomerRoleType.APPLICANT,
    actionTargetType: ActionTargetType.Applicant,
    matchingCustomersSelector: fromCustomerSearch.getApplicantSelectors.getMatchingCustomers,
    customersLoadingSelector: fromCustomerSearch.getApplicantSelectors.getLoading,
    saveAction: customer => new SetCustomer(ActionTargetType.Applicant, customer)
  },
  REPRESENTATIVE: {
    formName: 'representative',
    roleType: CustomerRoleType.REPRESENTATIVE,
    actionTargetType: ActionTargetType.Representative,
    matchingCustomersSelector: fromCustomerSearch.getRepresentativeSelectors.getMatchingCustomers,
    customersLoadingSelector: fromCustomerSearch.getRepresentativeSelectors.getLoading,
    saveAction: customer => new SetCustomer(ActionTargetType.Representative, customer)
  },
  PROPERTY_DEVELOPER: {
    formName: 'propertyDeveloper',
    roleType: CustomerRoleType.PROPERTY_DEVELOPER,
    actionTargetType: ActionTargetType.PropertyDeveloper,
    matchingCustomersSelector: fromCustomerSearch.getPropertyDeveloperSelectors.getMatchingCustomers,
    customersLoadingSelector: fromCustomerSearch.getPropertyDeveloperSelectors.getLoading,
    saveAction: customer => new SetCustomer(ActionTargetType.PropertyDeveloper, customer)
  },
  CONTRACTOR: {
    formName: 'contractor',
    roleType: CustomerRoleType.CONTRACTOR,
    actionTargetType: ActionTargetType.Contractor,
    matchingCustomersSelector: fromCustomerSearch.getContractorSelectors.getMatchingCustomers,
    customersLoadingSelector: fromCustomerSearch.getContractorSelectors.getLoading,
    saveAction: customer => new SetCustomer(ActionTargetType.Contractor, customer)
  },
  INVOICING_CUSTOMER: {
    formName: 'invoicingCustomer',
    roleType: undefined,
    actionTargetType: ActionTargetType.InvoicingCustomer,
    matchingCustomersSelector: fromCustomerSearch.getInvoicingCustomerSelectors.getMatchingCustomers,
    customersLoadingSelector: fromCustomerSearch.getInvoicingCustomerSelectors.getLoading,
    saveAction: customer => new SetCustomer(ActionTargetType.InvoicingCustomer, customer)
  },
};

