import {Injectable} from '@angular/core';
import {Action, Store} from '@ngrx/store';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {catchError, filter, map, switchMap, withLatestFrom} from 'rxjs/operators';
import {Observable} from 'rxjs/Observable';
import {of} from 'rxjs/observable/of';
import * as fromApplication from '../../application/reducers';
import * as fromProject from '../../project/reducers';
import * as fromComment from '../reducers/comment-reducer';
import {
  CommentAction,
  CommentActionType,
  Load,
  LoadFailed,
  LoadSuccess,
  Remove,
  RemoveFailed,
  RemoveSuccess, Save, SaveFailed, SaveSuccess
} from '../actions/comment-actions';
import {CommentService} from '../../../service/application/comment/comment.service';
import {Comment} from '../../../model/application/comment/comment';
import {CommentTargetType} from '../../../model/application/comment/comment-target-type';

@Injectable()
export class CommentEffects {
  constructor(private actions: Actions,
              private store: Store<fromComment.State>,
              private commentService: CommentService) {}

  @Effect()
  loadApplicationComments: Observable<Action> = this.actions.pipe(
    this.ofTargetAndType<Load>(CommentTargetType.Application, CommentActionType.Load),
    switchMap(([action, app]) => this.loadByType(action.targetType, app.id))
  );

  @Effect()
  saveApplicationComment: Observable<Action> = this.actions.pipe(
    this.ofTargetAndType<Save>(CommentTargetType.Application, CommentActionType.Save),
    switchMap(([action, app]) => this.saveByType(action.targetType, app.id, action.payload))
  );

  @Effect()
  loadProjectComments: Observable<Action> = this.actions.pipe(
    this.ofTargetAndType<Load>(CommentTargetType.Project, CommentActionType.Load),
    switchMap(([action, app]) => this.loadByType(action.targetType, app.id))
  );

  @Effect()
  saveProjectComment: Observable<Action> = this.actions.pipe(
    this.ofTargetAndType<Save>(CommentTargetType.Project, CommentActionType.Save),
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
    return this.commentService.getCommentsFor(type, id).pipe(
      map(comments => new LoadSuccess(type, comments)),
      catchError(error => of(new LoadFailed(type, error)))
    );
  }

  private saveByType(type: CommentTargetType, targetId: number, comment: Comment): Observable<Action> {
    return this.commentService.saveComment(type, targetId, comment).pipe(
      map(saved => new SaveSuccess(type, saved)),
      catchError(error => of(new SaveFailed(type, error)))
    );
  }

  private ofTargetAndType<T extends CommentAction>(targetType: CommentTargetType, ...allowedTypes: string[]) {
    const latestTarget = targetType === CommentTargetType.Application
      ? this.store.select(fromApplication.getCurrentApplication)
      : this.store.select(fromProject.getCurrentProject);

    return (source: Observable<CommentAction>) => source.pipe(
      ofType<T>(...allowedTypes),
      filter(action => targetType === action.targetType),
      withLatestFrom(latestTarget),
      filter(([action, target]) => target.id !== undefined)
    );
  }
}


