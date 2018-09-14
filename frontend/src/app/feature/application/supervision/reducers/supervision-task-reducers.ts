import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {SupervisionTask} from '@model/application/supervision/supervision-task';
import {SupervisionTaskActions, SupervisionTaskActionType} from '@feature/application/supervision/actions/supervision-task-actions';
import {TimeUtil} from '@util/time.util';

export interface State extends EntityState<SupervisionTask> {
  selectedId: number;
  loading: boolean;
}

export function sortByCreationTime(left: SupervisionTask, right: SupervisionTask): number {
  return TimeUtil.compareTo(right.creationTime, left.creationTime); // latest first
}

export const adapter: EntityAdapter<SupervisionTask> = createEntityAdapter<SupervisionTask>({
  sortComparer: sortByCreationTime
});

const initialState: State = adapter.getInitialState({
  selectedId: undefined,
  loading: false
});

export function reducer(state: State = initialState, action: SupervisionTaskActions) {
  switch (action.type) {
    case SupervisionTaskActionType.Load: {
      return {
        ...state,
        loading: true
      };
    }

    case SupervisionTaskActionType.LoadSuccess: {
      return adapter.addAll(action.payload, {
        ...state,
        loading: false
      });
    }

    case SupervisionTaskActionType.LoadFailed: {
      return {
        ...state,
        loading: false
      };
    }

    case SupervisionTaskActionType.SaveSuccess:
    case SupervisionTaskActionType.ApproveSuccess:
    case SupervisionTaskActionType.RejectSuccess: {
      return adapter.upsertOne(action.payload, state);
    }

    case SupervisionTaskActionType.RemoveSuccess: {
      return adapter.removeOne(action.payload, state);
    }

    default: {
      return {...state};
    }
  }
}

export const getSelectedTaskId = (state: State) => state.selectedId;
