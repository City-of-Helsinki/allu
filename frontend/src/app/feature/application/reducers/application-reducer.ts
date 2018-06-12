import {Application} from '../../../model/application/application';
import {ApplicationActions, ApplicationActionType} from '../actions/application-actions';
import {InvoicingActions, InvoicingActionType} from '../actions/invoicing-actions';
import {ObjectUtil} from '../../../util/object.util';
import {StructureMeta} from '../../../model/application/meta/structure-meta';
import {ApplicationMetaActions, ApplicationMetaActionType} from '../actions/application-meta-actions';

export interface State {
  loaded: boolean;
  loading: boolean;
  current: Application;
  meta: StructureMeta;
}

const initialState: State = {
  loaded: false,
  loading: false,
  current: new Application(),
  meta: undefined
};

type HandledActions = ApplicationActions | InvoicingActions | ApplicationMetaActions;

export function reducer(state: State = initialState, action: HandledActions) {
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

    case ApplicationMetaActionType.LoadSuccess: {
      return {
        ...state,
        meta: action.payload
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

export const getMeta = (state: State) => state.meta;

