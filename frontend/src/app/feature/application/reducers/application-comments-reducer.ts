import {initialState, reducer as commentReducer} from '../../comment/reducers/comment-reducer';
import {State} from '../../comment/reducers/comment-reducer';
import {CommentActions} from '../../comment/actions/comment-actions';
import {ActionTargetType} from '../../allu/actions/action-target-type';

export function reducer(state: State = initialState, action: CommentActions) {
  if (ActionTargetType.Application === action.targetType) {
    return commentReducer(state, action);
  } else {
    return {...state};
  }
}
