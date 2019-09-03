import {Component, Input, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {Observable, of} from 'rxjs';

import {Comment} from '../../model/application/comment/comment';
import {NotificationService} from '../notification/notification.service';
import {CommentService} from '../../service/application/comment/comment.service';
import {ActionTargetType} from '../allu/actions/action-target-type';
import {catchError} from 'rxjs/internal/operators';

@Component({
  selector: 'comments-modal',
  templateUrl: './comments-modal.component.html',
  styleUrls: []
})
export class CommentsModalComponent implements OnInit {

  @Input() applicationId: number;
  comments: Observable<Array<Comment>>;

  constructor(public dialogRef: MatDialogRef<CommentsModalComponent>,
              private commentService: CommentService,
              private notification: NotificationService) {
  }

  ngOnInit(): void {
    this.comments = this.commentService.getCommentsFor(ActionTargetType.Application, this.applicationId).pipe(
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
