import {InvoicingCustomerActions, InvoicingCustomerActionType} from '@feature/application/invoicing/actions/invoicing-customer-actions';
import {Customer} from '@model/customer/customer';

export interface State {
  customer: Customer;
  loading: boolean;
}

export const initialState: State = {
  customer: undefined,
  loading: false
};

export function reducer(state: State = initialState, action: InvoicingCustomerActions) {
  switch (action.type) {
    case InvoicingCustomerActionType.Load: {
      return {
        ...state,
        customer: undefined,
        loading: true
      };
    }

    case InvoicingCustomerActionType.LoadSuccess: {
      return {
        ...state,
        customer: action.payload,
        loading: false
      };
    }

    default: {
      return {
        ...state
      };
    }
  }
}

export const getCustomer = (state: State) => state.customer;
