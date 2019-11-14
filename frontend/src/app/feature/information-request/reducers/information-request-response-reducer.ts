import {InformationRequestResponse} from '@model/information-request/information-request-response';
import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {
  InformationRequestResponseAction,
  InformationRequestResponseActionType
} from '@feature/information-request/actions/information-request-response-actions';

export interface State extends EntityState<InformationRequestResponse> {
  loading: boolean;
}

export const adapter: EntityAdapter<InformationRequestResponse> = createEntityAdapter<InformationRequestResponse>({
  // response does not have separate id but only one response can exist for request
  // so it is safe to use requestId as id
  selectId: response => response.informationRequestId
});

export const initialState: State = adapter.getInitialState({
  loading: false
});

export function reducer(state: State = initialState, action: InformationRequestResponseAction) {
  switch (action.type) {
    case InformationRequestResponseActionType.LoadResponse: {
      return {
        ...state,
        loading: true
      };
    }

    case InformationRequestResponseActionType.LoadResponseFailed: {
      return adapter.removeAll({...state});
    }

    case InformationRequestResponseActionType.LoadResponseSuccess: {
      return adapter.upsertOne(action.payload, {
        ...state,
        loading: false
      });
    }

    default: {
      return state;
    }
  }
}


export const {
  selectIds: selectResponseIds,
  selectEntities: selectResponseEntities,
  selectAll: selectAllResponses,
  selectTotal: selectResponsesTotal
} = adapter.getSelectors();

export const getResponse = (id: number) => {
  return (state: State) => selectResponseEntities(state)[id];
};

export const getLoading = (state: State) => state.loading;
