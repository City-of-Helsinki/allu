import {Injectable} from '@angular/core';
import {Action, Store} from '@ngrx/store';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {catchError, filter, map, switchMap, withLatestFrom} from 'rxjs/operators';
import {Observable} from 'rxjs/Observable';
import {of} from 'rxjs/observable/of';
import * as fromApplication from '../../application/reducers';
import * as fromComment from '../reducers/comment-reducer';
import {
  CommentActionType,
  CommentTargetType,
  Load,
  LoadFailed,
  LoadSuccess,
  Remove,
  RemoveFailed,
  RemoveSuccess, Save, SaveFailed, SaveSuccess
} from '../actions/comment-actions';
import {CommentService} from '../../../service/application/comment/comment.service';
import {Comment} from '../../../model/application/comment/comment';

@Injectable()
export class CommentEffects {
  constructor(private actions: Actions,
              private store: Store<fromComment.State>,
              private commentService: CommentService) {}

  @Effect()
  loadApplicationComments: Observable<Action> = this.actions.pipe(
    ofType<Load>(CommentActionType.Load),
    filter(action => CommentTargetType.Application === action.targetType),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, target]) => target.id !== undefined),
    switchMap(([action, target]) => this.loadByType(action.targetType, target.id))
  );

  @Effect()
  saveApplicationComment: Observable<Action> = this.actions.pipe(
    ofType<Save>(CommentActionType.Save),
    filter(action => CommentTargetType.Application === action.targetType),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    switchMap(([action, app]) => this.saveByType(action.targetType, app.id, action.payload))
  );

  @Effect()
  remove: Observable<Action> = this.actions.pipe(
    ofType<Remove>(CommentActionType.Remove),
    switchMap(action => this.commentService.remove(action.payload).pipe(
      map(() => new RemoveSuccess(action.targetType, action.payload)),
      catchError(error => of(new RemoveFailed(action.targetType, error)))
    ))
  );

  private loadByType(type: CommentTargetType, id): Observable<Action> {
    return this.commentService.getComments(id).pipe(
      map(comments => new LoadSuccess(type, comments)),
      catchError(error => of(new LoadFailed(type, error)))
    );
  }

  private saveByType(type: CommentTargetType, targetId: number, comment: Comment): Observable<Action> {
    return this.commentService.save(targetId, comment).pipe(
      map(saved => new SaveSuccess(type, saved)),
      catchError(error => of(new SaveFailed(type, error)))
    );
  }
}
