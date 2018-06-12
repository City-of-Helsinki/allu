import {HistoryActions} from '../../history/actions/history-actions';
import {initialState, State, reducer as historyReducer} from '../../history/reducers/history-reducer';
import {ActionTargetType} from '../../allu/actions/action-target-type';

export function reducer(state: State = initialState, action: HistoryActions) {
  return ActionTargetType.Application === action.targetType
    ? historyReducer(state, action)
    : {...state};
}
