import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';

import {Comment} from '../../../model/application/comment/comment';
import {manualCommentNames} from '../../../model/application/comment/comment-type';

@Component({
  selector: 'comment',
  template: require('./comment.component.html'),
  styles: [
    require('./comment.component.scss')
  ]
})
export class CommentComponent implements OnInit {
  @Input() comment = new Comment();
  @Output() onRemove = new EventEmitter<Comment>();
  @Output() onSave = new EventEmitter<Comment>();

  _edit = false;
  commentTypes = manualCommentNames;

  private originalComment: Comment;

  constructor() {}

  ngOnInit() {
    this._edit = this.comment.id === undefined;
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
