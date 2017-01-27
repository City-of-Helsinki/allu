import {Component, OnInit, AfterViewInit, Input, Output, EventEmitter} from '@angular/core';
import {MaterializeUtil} from '../../../util/materialize.util';

import {Comment} from '../../../model/application/comment/comment';
import {manualCommentNames} from '../../../model/application/comment/comment-type';

@Component({
  selector: 'comment',
  template: require('./comment.component.html'),
  styles: [
    require('./comment.component.scss')
  ]
})
export class CommentComponent implements OnInit, AfterViewInit {
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

  ngAfterViewInit(): void {
    MaterializeUtil.updateTextFields(50);
  }

  remove(): void {
    this.onRemove.emit(this.comment);
  }

  save(): void {
    this._edit = false;
    this.onSave.emit(this.comment);
    MaterializeUtil.updateTextFields(50);
  }

  cancel(): void {
    this._edit = false;
    this.comment = this.originalComment.copy();
    this.originalComment = undefined;
  }

  editComment(): void {
    this._edit = true;
    MaterializeUtil.updateTextFields(50);
    MaterializeUtil.resizeTextArea('#commentText');
    this.originalComment = this.comment.copy();
  }

  get edit(): boolean {
    return this._edit || this.comment.id === undefined;
  }
}
