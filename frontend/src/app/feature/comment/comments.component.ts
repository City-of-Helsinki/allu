import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {MatDialog} from '@angular/material';
import {Store} from '@ngrx/store';
import {Load, Remove, Save} from './actions/comment-actions';
import {CommentTargetType} from '../../model/application/comment/comment-target-type';
import {CommentListComponent} from './comment-list.component';
import {findTranslation} from '../../util/translations';
import {ConfirmDialogComponent} from '../common/confirm-dialog/confirm-dialog.component';
import {Comment} from '../../model/application/comment/comment';
import * as fromApplication from '../application/reducers';
import * as fromProject from '../project/reducers';
import * as fromRoot from '../allu/reducers';
import {of} from 'rxjs/observable/of';

@Component({
  selector: 'comments',
  templateUrl: './comments.component.html',
  styleUrls: []
})
export class CommentsComponent implements OnInit {
  @Input() targetType: CommentTargetType;
  comments$: Observable<Comment[]>;

  @ViewChild(CommentListComponent) commentListComponent: CommentListComponent;

  constructor(private dialog: MatDialog,
              private store: Store<fromRoot.State>) {}

  ngOnInit(): void {
    this.comments$ = this.targetType === CommentTargetType.Application
      ? this.store.select(fromApplication.getAllComments)
      : this.store.select(fromProject.getAllComments);
  }

  save(comment: Comment): void {
    this.store.dispatch(new Save(this.targetType, comment));
  }

  remove(comment: Comment): void {
    this.store.dispatch(new Remove(this.targetType, comment.id));
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
