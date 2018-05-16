import {initialState, reducer as commentReducer} from '../../comment/reducers/comment-reducer';
import {State} from '../../comment/reducers/comment-reducer';
import {CommentActions} from '../../comment/actions/comment-actions';
import {CommentTargetType} from '../../../model/application/comment/comment-target-type';

export function reducer(state: State = initialState, action: CommentActions) {
  if (CommentTargetType.Project === action.targetType) {
    return commentReducer(state, action);
  } else {
    return {...state};
  }
}
