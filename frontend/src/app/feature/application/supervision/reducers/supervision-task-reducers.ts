import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {SupervisionTask} from '@model/application/supervision/supervision-task';
import {SupervisionTaskActions, SupervisionTaskActionType} from '@feature/application/supervision/actions/supervision-task-actions';
import {TimeUtil} from '@util/time.util';

export interface State extends EntityState<SupervisionTask> {
  selectedId: number;
  loading: boolean;
  saving: boolean;
}

export function sortByCreationTime(left: SupervisionTask, right: SupervisionTask): number {
  return TimeUtil.compareTo(left.plannedFinishingTime, right.plannedFinishingTime);
}

export const adapter: EntityAdapter<SupervisionTask> = createEntityAdapter<SupervisionTask>({
  sortComparer: sortByCreationTime
});

const initialState: State = adapter.getInitialState({
  selectedId: undefined,
  loading: false,
  saving: false
});

export function reducer(state: State = initialState, action: SupervisionTaskActions) {
  switch (action.type) {
    case SupervisionTaskActionType.Load: {
      return adapter.removeAll({
        ...state,
        loading: true
      });
    }

    case SupervisionTaskActionType.LoadSuccess: {
      return adapter.setAll(action.payload, {
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

    case SupervisionTaskActionType.Save:
    case SupervisionTaskActionType.Approve:
    case SupervisionTaskActionType.Reject: {
      return {
        ...state,
        saving: true
      };
    }

    case SupervisionTaskActionType.SaveSuccess:
    case SupervisionTaskActionType.ApproveSuccess:
    case SupervisionTaskActionType.RejectSuccess: {
      return adapter.upsertOne(action.payload, {
        ...state,
        saving: false
      });
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

export const getSaving = (state: State) => state.saving;

export const getLoading = (state: State) => state.loading;
