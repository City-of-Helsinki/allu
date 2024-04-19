import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Observable, of} from 'rxjs';
import {MatLegacyDialog as MatDialog} from '@angular/material/legacy-dialog';
import {Store} from '@ngrx/store';
import {Remove, Save, ToggleDirection} from './actions/comment-actions';
import {CommentListComponent} from './comment-list.component';
import {findTranslation} from '@util/translations';
import {ConfirmDialogComponent} from '@feature/common/confirm-dialog/confirm-dialog.component';
import {Comment} from '@model/application/comment/comment';
import * as fromApplication from '../application/reducers';
import * as fromProject from '../project/reducers';
import * as fromRoot from '../allu/reducers';
import {SortDirection} from '@model/common/sort';
import {ActionTargetType} from '../allu/actions/action-target-type';

@Component({
  selector: 'comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.scss']
})
export class CommentsComponent implements OnInit {
  @Input() targetType: ActionTargetType;
  comments$: Observable<Comment[]>;
  loading$: Observable<boolean>;
  direction$: Observable<SortDirection>;

  @ViewChild(CommentListComponent) commentListComponent: CommentListComponent;

  constructor(private dialog: MatDialog,
              private store: Store<fromRoot.State>) {}

  ngOnInit(): void {
    const target = this.targetType === ActionTargetType.Application
      ? fromApplication
      : fromProject;

    this.comments$ = this.store.select(target.getSortedComments);
    this.loading$ = this.store.select(target.getCommentsLoading);
    this.direction$ = this.store.select(target.getDirection);
  }

  save(comment: Comment): void {
    this.store.dispatch(new Save(this.targetType, comment));
  }

  remove(comment: Comment): void {
    this.store.dispatch(new Remove(this.targetType, comment.id));
  }

  toggleDirection(): void {
    this.store.dispatch(new ToggleDirection(this.targetType));
  }

  canDeactivate(): Observable<boolean> {
    if (this.commentListComponent.dirty) {
      return this.confirmChanges();
    } else {
      return of(true);
    }
  }

  private confirmChanges(): Observable<boolean> {
    const data = {
      title: findTranslation(['comment.confirmDiscard.title']),
      description: findTranslation(['comment.confirmDiscard.description']),
      confirmText: findTranslation(['comment.confirmDiscard.confirmText']),
      cancelText: findTranslation(['comment.confirmDiscard.cancelText'])
    };
    return this.dialog.open(ConfirmDialogComponent, {data}).afterClosed();
  }
}
