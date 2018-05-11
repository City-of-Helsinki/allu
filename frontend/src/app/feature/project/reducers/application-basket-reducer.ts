import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {Application} from '../../../model/application/application';
import {ApplicationBasketActions, ApplicationBasketActionType} from '../actions/application-basket-actions';

export interface State extends EntityState<Application> {
  selectedId: number;
  pending: number[];
}

export const adapter: EntityAdapter<Application> = createEntityAdapter<Application>({
  selectId: (application: Application) => application.id
});

export const initialState: State = adapter.getInitialState({
  selectedId: undefined,
  pending: []
});

export function reducer(state: State = initialState, action: ApplicationBasketActions) {
  switch (action.type) {
    case ApplicationBasketActionType.LoadSuccess: {
      return adapter.addMany(action.payload, {
        ...state,
        selectedId: state.selectedId
      });
    }

    case ApplicationBasketActionType.Remove: {
      return adapter.removeOne(action.payload, {
        ...state,
        selectedId: state.selectedId,
        pending: state.pending.filter(id => id !== action.payload)
      });
    }

    case ApplicationBasketActionType.Clear: {
      return adapter.removeAll({
        ...state,
        pending: [],
        selectedId: state.selectedId
      });
    }

    case ApplicationBasketActionType.CreateProject: {
      return {
        ...state,
        pending: state.ids
      };
    }

    default: {
      return {
        ...state
      };
    }
  }
}
