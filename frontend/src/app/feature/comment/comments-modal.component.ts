import {Component, OnInit, Input} from '@angular/core';
import {MatDialogRef} from '@angular/material';
import {Observable} from 'rxjs/Observable';

import {Comment} from '../../model/application/comment/comment';
import {NotificationService} from '../../service/notification/notification.service';
import {CommentService} from '../../service/application/comment/comment.service';
import {ActionTargetType} from '../allu/actions/action-target-type';

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
    this.comments = this.commentService.getCommentsFor(ActionTargetType.Application, this.applicationId)
      .catch(err => {
        this.notification.errorInfo(err);
        return Observable.of([]);
      });
  }

  close() {
    this.dialogRef.close([]);
  }
}
