import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {FormGroup} from '@angular/forms';

import {Comment} from '../../../model/application/comment/comment';
import {manualCommentNames} from '../../../model/application/comment/comment-type';
import {Some} from '../../../util/option';
import {CurrentUser} from '../../../service/user/current-user';
import {CommentForm} from './comment-form';
import {TimeUtil} from '../../../util/time.util';

@Component({
  selector: 'comment',
  templateUrl: './comment.component.html',
  styleUrls: [
    './comment.component.scss'
  ]
})
export class CommentComponent implements OnInit {
  @Input() form: FormGroup;
  @Output() onRemove = new EventEmitter<Comment>();
  @Output() onSave = new EventEmitter<Comment>();

  commentTypes = manualCommentNames;
  canEdit = false;

  private originalComment: CommentForm;

  constructor(private currentUser: CurrentUser) {
  }

  ngOnInit() {
    const formValue = this.form.value;
    this.currentUser.user.subscribe(current => {
      this.canEdit = Some(formValue.user).map(user => user.id === current.id).orElse(false);
    });
    if (formValue.id) {
      this.form.disable();
    }
  }

  remove(): void {
    const formValue = <CommentForm>this.form.value;
    this.onRemove.emit(CommentForm.to(formValue));
  }

  save(): void {
    const formValue = <CommentForm>this.form.value;
    this.onSave.emit(CommentForm.to(formValue));
    this.form.disable();
  }

  cancel(): void {
    if (this.originalComment) {
      this.form.patchValue(this.originalComment);
      this.originalComment = undefined;
      this.form.disable();
    } else {
      this.onRemove.emit();
    }
  }

  editComment(): void {
    this.form.enable();
    this.originalComment = this.form.value;
  }
}
