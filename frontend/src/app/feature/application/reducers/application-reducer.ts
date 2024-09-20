import {Application} from '@model/application/application';
import {ApplicationActions, ApplicationActionType} from '@feature/application/actions/application-actions';
import {ObjectUtil} from '@util/object.util';
import {StructureMeta} from '@model/application/meta/structure-meta';
import {ApplicationMetaActions, ApplicationMetaActionType} from '@feature/application/actions/application-meta-actions';
import {ClientApplicationData} from '@model/application/client-application-data';
import {ApplicationType} from '@model/application/type/application-type';
import {KindsWithSpecifiers} from '@model/application/type/application-specifier';
import {InvoicingCustomerActions, InvoicingCustomerActionType} from '@feature/application/invoicing/actions/invoicing-customer-actions';
import {DistributionEntry} from '@model/common/distribution-entry';
import {ArrayUtil} from '@util/array-util';

export interface State {
  loaded: boolean;
  loading: boolean;
  current: Application;
  meta: StructureMeta;
  clientData: ClientApplicationData;
  type: ApplicationType;
  kindsWithSpecifiers: KindsWithSpecifiers;
  distribution: DistributionEntry[];
  replacedDisableRemoveButton: boolean;
}

const initialState: State = {
  loaded: false,
  loading: false,
  current: new Application(),
  meta: undefined,
  clientData: undefined,
  type: undefined,
  kindsWithSpecifiers: undefined,
  distribution: [],
  replacedDisableRemoveButton: false,
};

type HandledActions = ApplicationActions | InvoicingCustomerActions | ApplicationMetaActions;

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
      const application = action.payload;
      return {
        ...state,
        loading: false,
        loaded: true,
        current: application,
        clientData: application.clientApplicationData,
        type: application.type,
        kindsWithSpecifiers: application.kindsWithSpecifiers,
        replacedDisableRemoveButton: false,
      };
    }

    case ApplicationActionType.LoadFailed: {
      return {
        ...state,
        loading: false,
        loaded: true
      };
    }

    case ApplicationActionType.SetType: {
      return {
        ...state,
        type: action.payload
      };
    }

    case ApplicationActionType.SetKindsWithSpecifiers: {
      return {
        ...state,
        kindsWithSpecifiers: action.payload
      };
    }

    case ApplicationMetaActionType.LoadSuccess: {
      return {
        ...state,
        meta: action.payload
      };
    }

    case InvoicingCustomerActionType.SetRecipientSuccess: {
      const application = ObjectUtil.clone(state.current);
      application.invoiceRecipientId = action.payload;
      return {
        ...state,
        current: application
      };
    }

    case ApplicationActionType.SaveDistributionAndNotify:
    case ApplicationActionType.SaveDistribution:
    case ApplicationActionType.LoadDistributionSuccess:
    case ApplicationActionType.SaveDistributionSuccess: {
      return {
        ...state,
        distribution: action.payload
      };
    }

    case ApplicationActionType.AddToDistribution: {
      const updatedDistribution = [...state.distribution]
        .concat(action.payload)
        .filter(ArrayUtil.uniqueItem(item => item.email));

      return {
        ...state,
        distribution: updatedDistribution
      };
    }

    case ApplicationActionType.Replaced:
      return {
        ...state,
        replacedDisableRemoveButton: true
      }
    
    default:
      return {...state};
  }
}

export const getRemoveButtonDisableStatus = (state: State) => state.replacedDisableRemoveButton;

export const getCurrent = (state: State) => state.current;

export const getLoaded = (state: State) => state.loaded;

export const getType = (state: State) => state.type;

export const getKindsWithSpecifiers = (state: State) => state.kindsWithSpecifiers;

export const getClientData = (state: State) => state.clientData;

export const getMeta = (state: State) => state.meta;

export const getDistribution = (state: State) => state.distribution;

