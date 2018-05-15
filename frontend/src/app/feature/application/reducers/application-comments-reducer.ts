import {initialState, reducer as commentReducer} from '../../comment/reducers/comment-reducer';
import {State} from '../../comment/reducers/comment-reducer';
import {CommentActions, CommentTargetType} from '../../comment/actions/comment-actions';

export function reducer(state: State = initialState, action: CommentActions) {
  if (CommentTargetType.Application === action.targetType) {
    return commentReducer(state, action);
  } else {
    return {...state};
  }
}
