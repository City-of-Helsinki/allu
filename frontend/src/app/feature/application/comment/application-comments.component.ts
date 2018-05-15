import {Component, OnInit, ViewChild} from '@angular/core';
import {Comment} from '../../../model/application/comment/comment';
import {Observable} from 'rxjs/Observable';
import {findTranslation} from '../../../util/translations';
import {ConfirmDialogComponent} from '../../common/confirm-dialog/confirm-dialog.component';
import {CanComponentDeactivate} from '../../../service/common/can-deactivate-guard';
import {CommentsComponent} from '../../comment/comments.component';
import {MatDialog} from '@angular/material';
import {Store} from '@ngrx/store';
import * as fromApplication from '../reducers';
import {CommentTargetType, Load, Remove, Save} from '../../comment/actions/comment-actions';

@Component({
  selector: 'application-comments',
  templateUrl: './application-comments.component.html',
  styleUrls: []
})
export class ApplicationCommentsComponent implements OnInit, CanComponentDeactivate {
  comments$: Observable<Comment[]>;

  @ViewChild(CommentsComponent) commentsComponent: CommentsComponent;

  constructor(private dialog: MatDialog,
              private store: Store<fromApplication.State>) {}

  ngOnInit(): void {
    this.comments$ = this.store.select(fromApplication.getAllComments);
  }

  save(comment: Comment): void {
    this.store.dispatch(new Save(CommentTargetType.Application, comment));
  }

  remove(comment: Comment): void {
    this.store.dispatch(new Remove(CommentTargetType.Application, comment.id));
  }

  canDeactivate(): Observable<boolean> | boolean {
    if (this.commentsComponent.dirty) {
      return this.confirmChanges();
    } else {
      return true;
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
