import {Application} from '../../../model/application/application';
import {ApplicationActions, ApplicationActionType} from '../actions/application-actions';
import {InvoicingActions, InvoicingActionType} from '../actions/invoicing-actions';
import {ObjectUtil} from '../../../util/object.util';

export interface State {
  loaded: boolean;
  loading: boolean;
  current: Application;
}

const initialState: State = {
  loaded: false,
  loading: false,
  current: new Application()
};

export function reducer(state: State = initialState, action: ApplicationActions | InvoicingActions) {
  switch (action.type) {
    case ApplicationActionType.Load: {
      return {
        ...state,
        loading: true,
        loaded: false
      };
    }

    case ApplicationActionType.LoadSuccess: {
      return {
        ...state,
        loading: false,
        loaded: true,
        current: action.payload
      };
    }

    case ApplicationActionType.LoadFailed: {
      return {
        ...state,
        loading: false,
        loaded: true
      };
    }

    case InvoicingActionType.SetRecipientSuccess: {
      const application = ObjectUtil.clone(state.current);
      application.invoiceRecipientId = action.payload;
      return {
        ...state,
        current: application
      };
    }

    default:
      return {...state};
  }
}

export const getCurrent = (state: State) => state.current;

export const getLoaded = (state: State) => state.loaded;

