import {Component, OnInit, Input} from '@angular/core';
import {MdDialogRef} from '@angular/material';
import {Observable} from 'rxjs/Observable';

import {CommentHub} from '../../../service/application/comment/comment-hub';
import {Comment} from '../../../model/application/comment/comment';
import {NotificationService} from '../../../service/notification/notification.service';

@Component({
  selector: 'comments-modal',
  template: require('./comments-modal.component.html'),
  styles: []
})
export class CommentsModalComponent implements OnInit {

  @Input() applicationId: number;
  comments: Observable<Array<Comment>>;

  constructor(public dialogRef: MdDialogRef<CommentsModalComponent>,
              private commentHub: CommentHub) {
  }

  ngOnInit(): void {
    this.comments = this.commentHub.getComments(this.applicationId)
      .catch(err => {
        NotificationService.error(err);
        return Observable.empty();
      });
  }

  close() {
    this.dialogRef.close([]);
  }
}
