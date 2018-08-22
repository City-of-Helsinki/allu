import {Application} from '../../../model/application/application';
import {InformationRequestResultActions, InformationRequestResultActionType} from '../actions/information-request-result-actions';
import {Customer} from '../../../model/customer/customer';
import {Contact} from '../../../model/customer/contact';
import {KindsWithSpecifiers} from '../../../model/application/type/application-specifier';
import {ArrayUtil} from '@util/array-util';

export interface State {
  application: Application;
  customer: Customer;
  contacts: Contact[];
  kindsWithSpecifiers: KindsWithSpecifiers;
  invoicingCustomer: Customer;
}

export const initialState: State = {
  application: undefined,
  customer: undefined,
  contacts: [],
  kindsWithSpecifiers: {},
  invoicingCustomer: undefined
};

export function reducer(state: State = initialState, action: InformationRequestResultActions) {
  switch (action.type) {
    case InformationRequestResultActionType.SetApplication: {
      return {
        ...state,
        application: action.payload
      };
    }

    case InformationRequestResultActionType.SetCustomer: {
      return {
        ...state,
        customer: action.payload
      };
    }

    case InformationRequestResultActionType.SetContacts: {
      return {
        ...state,
        contacts: action.payload
      };
    }

    case InformationRequestResultActionType.SetContact: {
      const index = action.payload.index;
      const contact = action.payload.contact;
      const result = ArrayUtil.createOrReplaceAt(state.contacts, contact, index);
      return {
        ...state,
        contacts: result
      };
    }

    case InformationRequestResultActionType.SetKindsWithSpecifiers: {
      return {
        ...state,
        kindsWithSpecifiers: action.payload
      };
    }

    case InformationRequestResultActionType.SetInvoicingCustomer: {
      return {
        ...state,
        invoicingCustomer: action.payload
      };
    }

    case InformationRequestResultActionType.SaveSuccess: {
      return initialState;
    }

    default: {
      return {
        ...state
      };
    }
  }
}

export const getApplication = (state: State) => state.application;

export const getCustomer = (state: State) => state.customer;

export const getContacts = (state: State) => state.contacts;

export const getKindsWithSpecifiers = (state: State) => state.kindsWithSpecifiers;

export const getInvoicingCustomer = (state: State) => state.invoicingCustomer;
