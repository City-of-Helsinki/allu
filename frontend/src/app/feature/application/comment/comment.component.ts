import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';

import {Comment} from '../../../model/application/comment/comment';
import {manualCommentNames} from '../../../model/application/comment/comment-type';
import {Some} from '../../../util/option';
import {CurrentUser} from '../../../service/user/current-user';

@Component({
  selector: 'comment',
  templateUrl: './comment.component.html',
  styleUrls: [
    './comment.component.scss'
  ]
})
export class CommentComponent implements OnInit {
  @Input() comment = new Comment();
  @Output() onRemove = new EventEmitter<Comment>();
  @Output() onSave = new EventEmitter<Comment>();

  _edit = false;
  commentTypes = manualCommentNames;
  canEdit = false;

  private originalComment: Comment;

  constructor(private currentUser: CurrentUser) {}

  ngOnInit() {
    this._edit = this.comment.id === undefined;

    this.currentUser.user.subscribe(current => {
      this.canEdit = Some(this.comment.user).map(user => user.id === current.id).orElse(false);
    });
  }

  remove(): void {
    this.onRemove.emit(this.comment);
  }

  save(): void {
    this._edit = false;
    this.onSave.emit(this.comment);
  }

  cancel(): void {
    this._edit = false;
    this.comment = this.originalComment.copy();
    this.originalComment = undefined;
  }

  editComment(): void {
    this._edit = true;
    this.originalComment = this.comment.copy();
  }

  get edit(): boolean {
    return this._edit || this.comment.id === undefined;
  }
}
