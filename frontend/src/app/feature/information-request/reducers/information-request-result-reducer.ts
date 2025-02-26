import {Application} from '@model/application/application';
import {
  InformationRequestResultActions,
  InformationRequestResultActionType,
  SetCustomer
} from '../actions/information-request-result-actions';
import {Customer} from '@model/customer/customer';
import {Contact} from '@model/customer/contact';
import {KindsWithSpecifiers} from '@model/application/type/application-specifier';
import {ArrayUtil} from '@util/array-util';
import {CustomerRoleType} from '@model/customer/customer-role-type';
import {FieldValues} from '@feature/information-request/acceptance/field-select/field-select.component';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {ObjectUtil} from '@util/object.util';
import {OrdererId} from '@model/application/cable-report/orderer-id';
import {Location} from '@model/common/location';
import {NumberUtil} from '@util/number.util';

export interface State {
  application: Application;
  applicant: Customer;
  representative: Customer;
  propertyDeveloper: Customer;
  contractor: Customer;
  contacts: Contact[];
  kindsWithSpecifiers: KindsWithSpecifiers;
  invoicingCustomer: Customer;
  useCustomerForInvoicing: CustomerRoleType;
  otherInfo: FieldValues;
  locations: Location[];
}

export const initialState: State = {
  application: undefined,
  applicant: undefined,
  representative: undefined,
  propertyDeveloper: undefined,
  contractor: undefined,
  contacts: [],
  kindsWithSpecifiers: {},
  invoicingCustomer: undefined,
  useCustomerForInvoicing: undefined,
  otherInfo: undefined,
  locations: []
};

function updateCustomer(state: State, action: SetCustomer): State {
  switch (action.targetType) {
    case ActionTargetType.Applicant:
      return {...state, applicant: action.payload};
    case ActionTargetType.Representative:
      return {...state, representative: action.payload};
    case ActionTargetType.PropertyDeveloper:
      return {...state, propertyDeveloper: action.payload};
    case ActionTargetType.Contractor:
      return {...state, contractor: action.payload};
    case ActionTargetType.InvoicingCustomer:
      return {...state, invoicingCustomer: action.payload};
    default:
      return state;
  }
}

export function reducer(state: State = initialState, action: InformationRequestResultActions) {
  switch (action.type) {
    case InformationRequestResultActionType.SetApplication: {
      return {
        ...state,
        application: action.payload
      };
    }

    case InformationRequestResultActionType.SetCustomer: {
      return updateCustomer(state, action);
    }

    case InformationRequestResultActionType.SetContact: {
      const contact = action.payload;
      const result = ArrayUtil.createOrReplace(state.contacts, contact, c => c.id === contact.id);

      if (contact.orderer) {
        return {
          ...state,
          application: ObjectUtil.set(state.application, 'extension.ordererId', OrdererId.ofId(contact.id)),
          contacts: result
        };
      } else {
        return {
          ...state,
          contacts: result
        };
      }
    }

    case InformationRequestResultActionType.SetKindsWithSpecifiers: {
      return {
        ...state,
        kindsWithSpecifiers: action.payload
      };
    }

    case InformationRequestResultActionType.UseCustomerForInvoicing: {
      return {
        ...state,
        useCustomerForInvoicing: action.payload
      };
    }

    case InformationRequestResultActionType.SetOtherInfo: {
      return {
        ...state,
        otherInfo: action.payload
      };
    }

    case InformationRequestResultActionType.SetLocations: {
      return {
        ...state,
        locations: action.payload
      };
    }

    case InformationRequestResultActionType.SetLocation: {
      const location = action.payload;
      return {
        ...state,
        locations: ArrayUtil.upsert(
          state.locations,
          action.payload,
          loc => NumberUtil.isExisting(location) && loc.locationKey === location.locationKey)
      };
    }

    case InformationRequestResultActionType.SaveSuccess: {
      return initialState;
    }

    case InformationRequestResultActionType.UpdateCustomerReference: {
      return {
        ...state,
        application: {
          ...state.application,
          customerReference: action.payload
        }
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

export const getApplicant = (state: State) => state.applicant;

export const getRepresentative = (state: State) => state.representative;

export const getPropertyDeveloper = (state: State) => state.propertyDeveloper;

export const getContractor = (state: State) => state.contractor;

export const getContacts = (state: State) => state.contacts;

export const getKindsWithSpecifiers = (state: State) => state.kindsWithSpecifiers;

export const getInvoicingCustomer = (state: State) => state.invoicingCustomer;

export const useCustomerForInvoicing = (state: State) => state.useCustomerForInvoicing;

export const getOtherInfo = (state: State) => state.otherInfo;
