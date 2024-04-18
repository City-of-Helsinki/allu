import {Injectable} from '@angular/core';
import {Action, Store} from '@ngrx/store';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {catchError, map, switchMap} from 'rxjs/operators';
import {from, Observable, of} from 'rxjs';

import * as fromComment from '../reducers/comment-reducer';
import {CommentActionType, Load, LoadFailed, LoadSuccess, Remove, RemoveSuccess, Save, SaveSuccess} from '../actions/comment-actions';
import {CommentService} from '@service/application/comment/comment.service';
import {Comment} from '@model/application/comment/comment';
import {ActionTargetType} from '../../allu/actions/action-target-type';
import {withLatestExistingOfTargetAndType} from '../../allu/actions/action-with-target';
import * as fromProject from '../../project/reducers';
import * as fromApplication from '../../application/reducers';
import {ApproveSuccess, RejectSuccess, ContractActionType} from '@feature/decision/actions/contract-actions';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';

@Injectable()
export class CommentEffects {
  constructor(private actions: Actions,
              private store: Store<fromComment.State>,
              private commentService: CommentService) {}

  
  loadApplicationComments: Observable<Action> = createEffect(() => this.actions.pipe(
    withLatestExistingOfTargetAndType<Load>(ActionTargetType.Application, this.currentApplication, CommentActionType.Load),
    switchMap(([action, app]) => this.loadByType(action.targetType, app.id))
  ));

  
  saveApplicationComment: Observable<Action> = createEffect(() => this.actions.pipe(
    withLatestExistingOfTargetAndType<Save>(ActionTargetType.Application, this.currentApplication, CommentActionType.Save),
    switchMap(([action, app]) => this.saveByType(action.targetType, app.id, action.payload))
  ));

  
  loadProjectComments: Observable<Action> = createEffect(() => this.actions.pipe(
    withLatestExistingOfTargetAndType<Load>(ActionTargetType.Project, this.currentProject, CommentActionType.Load),
    switchMap(([action, app]) => this.loadByType(action.targetType, app.id))
  ));

  
  saveProjectComment: Observable<Action> = createEffect(() => this.actions.pipe(
    withLatestExistingOfTargetAndType<Save>(ActionTargetType.Project, this.currentProject, CommentActionType.Save),
    switchMap(([action, app]) => this.saveByType(action.targetType, app.id, action.payload))
  ));

  
  remove: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Remove>(CommentActionType.Remove),
    switchMap(action => this.commentService.remove(action.payload).pipe(
      map(() => new RemoveSuccess(action.targetType, action.payload)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  contractApproved: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ApproveSuccess>(ContractActionType.ApproveSuccess),
    map(() => new Load(ActionTargetType.Application))
  ));

  
  contractRejected: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<RejectSuccess>(ContractActionType.RejectSuccess),
    map(() => new Load(ActionTargetType.Application))
  ));


  private loadByType(type: ActionTargetType, id): Observable<Action> {
    return this.commentService.getCommentsFor(type, id).pipe(
      map(comments => new LoadSuccess(type, comments)),
      catchError(error => from([
        new LoadFailed(type, error),
        new NotifyFailure(error)
      ]))
    );
  }

  private saveByType(type: ActionTargetType, targetId: number, comment: Comment): Observable<Action> {
    return this.commentService.saveComment(type, targetId, comment).pipe(
      map(saved => new SaveSuccess(type, saved)),
      catchError(error => of(new NotifyFailure(error)))
    );
  }

  private get currentApplication() {
    return this.store.select(fromApplication.getCurrentApplication);
  }

  private get currentProject() {
    return this.store.select(fromProject.getCurrentProject);
  }
}


