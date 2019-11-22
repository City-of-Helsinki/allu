import {InformationRequestAction, InformationRequestActionType} from '../actions/information-request-actions';
import {InformationRequest} from '@model/information-request/information-request';
import {InformationRequestStatus, isRequestUnfinished} from '@model/information-request/information-request-status';
import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';

export interface State extends EntityState<InformationRequest> {
  loading: boolean;
  active: number;
}

export const adapter: EntityAdapter<InformationRequest> = createEntityAdapter<InformationRequest>({
  selectId: request => request.informationRequestId
});

export const initialState: State = adapter.getInitialState({
  loading: false,
  active: undefined
});

const getUpdatedActiveOnUpsert = (state: State, request: InformationRequest): number => {
  if (isRequestUnfinished(request.status)) {
    return request.informationRequestId;
  } else if (request.status === InformationRequestStatus.CLOSED && request.informationRequestId === state.active) {
    return undefined;
  } else {
    return state.active;
  }
};

export function reducer(state: State = initialState, action: InformationRequestAction) {
  switch (action.type) {
    case InformationRequestActionType.LoadRequest: {
      return {
        ...state,
        loading: true
      };
    }

    case InformationRequestActionType.LoadActiveRequest: {
      return {
        ...state,
        loading: true,
        active: undefined
      };
    }

    case InformationRequestActionType.LoadRequestFailed: {
      return {
        ...state,
        loading: false
      };
    }

    case InformationRequestActionType.LoadRequestSuccess: {
      if (action.payload) {
        return adapter.upsertOne(action.payload, {
          ...state,
          loading: false,
          active: getUpdatedActiveOnUpsert(state, action.payload)
        });
      } else {
        return {
          ...state,
          loading: false,
          active: undefined
        };
      }
    }

    case InformationRequestActionType.SaveRequestSuccess: {
      return adapter.upsertOne(action.payload, {
        ...state,
        active: getUpdatedActiveOnUpsert(state, action.payload)
      });
    }

    case InformationRequestActionType.CloseRequest:
    case InformationRequestActionType.CancelRequestSuccess: {
      return adapter.removeOne(action.payload, {
        ...state,
        active: state.active === action.payload ? undefined : state.active
      });
    }

    default: {
      return state;
    }
  }
}

export const getActive = (state: State) => state.active;

export const {
  selectIds: selectRequestIds,
  selectEntities: selectRequestEntities,
  selectAll: selectAllRequests,
  selectTotal: selectRequestTotal
} = adapter.getSelectors();


export const getRequestLoading = (state: State) => state.loading;
