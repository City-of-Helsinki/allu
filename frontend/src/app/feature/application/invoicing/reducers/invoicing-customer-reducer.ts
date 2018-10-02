import {InvoicingCustomerActions, InvoicingCustomerActionType} from '@feature/application/invoicing/actions/invoicing-customer-actions';
import {Customer} from '@model/customer/customer';

export interface State {
  customer: Customer;
}

export const initialState: State = {
  customer: undefined
};

export function reducer(state: State = initialState, action: InvoicingCustomerActions) {
  switch (action.type) {
    case InvoicingCustomerActionType.LoadSuccess: {
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

export const getCustomer = (state: State) => state.customer;
