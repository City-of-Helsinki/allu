import {Application} from '../../../model/application/application';
import {InformationRequestResultActions, InformationRequestResultActionType} from '../actions/information-request-result-actions';
import {Customer} from '../../../model/customer/customer';
import {Contact} from '../../../model/customer/contact';

export interface State {
  application: Application;
  customer: Customer;
  contacts: Contact[];
}

export const initialState: State = {
  application: undefined,
  customer: undefined,
  contacts: []
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
