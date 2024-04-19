import {Component, Inject, OnInit} from '@angular/core';
import {MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA, MatLegacyDialogRef as MatDialogRef} from '@angular/material/legacy-dialog';
import {Observable, of} from 'rxjs';

import {Comment} from '@model/application/comment/comment';
import {NotificationService} from '../notification/notification.service';
import {CommentService} from '@service/application/comment/comment.service';
import {ActionTargetType} from '../allu/actions/action-target-type';
import {catchError} from 'rxjs/internal/operators';

export const COMMENTS_MODAL_CONFIG = {width: '800px', data: {}};

export interface CommentsModalData {
  applicationId: number;
}

@Component({
  selector: 'comments-modal',
  templateUrl: './comments-modal.component.html',
  styleUrls: []
})
export class CommentsModalComponent implements OnInit {
  comments$: Observable<Array<Comment>>;

  constructor(public dialogRef: MatDialogRef<CommentsModalComponent>,
              private commentService: CommentService,
              private notification: NotificationService,
              @Inject(MAT_DIALOG_DATA) public data: CommentsModalData) {
  }

  ngOnInit(): void {
    this.comments$ = this.commentService.getCommentsFor(ActionTargetType.Application, this.data.applicationId).pipe(
      catchError(err => {
        this.notification.errorInfo(err);
        return of([]);
      })
    );
  }

  close() {
    this.dialogRef.close([]);
  }
}
