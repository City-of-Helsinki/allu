import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {manualCommentNames} from '../../model/application/comment/comment-type';
import {CommentForm} from './comment-form';
import {Comment} from '../../model/application/comment/comment';
import {Store} from '@ngrx/store';
import * as fromAuth from '../auth/reducers';
import {NumberUtil} from '../../util/number.util';
import {StringUtil} from '../../util/string.util';
import {map, take, takeWhile} from 'rxjs/internal/operators';

@Component({
  selector: 'comment',
  templateUrl: './comment.component.html',
  styleUrls: [
    './comment.component.scss'
  ]
})
export class CommentComponent implements OnInit {
  @Input() comment: Comment = new Comment();
  @Output('save') onSave = new EventEmitter<Comment>();
  @Output('remove') onRemove = new EventEmitter<Comment>();

  canEdit = false;
  commentTypes = manualCommentNames;
  form: FormGroup;

  private originalForm: CommentForm;

  constructor(private store: Store<fromAuth.State>, private fb: FormBuilder) {
    this.form = this.fb.group({
      id: [undefined],
      type: [undefined],
      text: [undefined],
      createTime: [undefined],
      updateTime: [undefined],
      user: [undefined],
      commentator: [undefined]
    });
  }

  ngOnInit() {
    this.form.patchValue(CommentForm.from(this.comment));

    if (!this.isNew) {
      this.form.get('text').setValidators(Validators.required);
      this.form.disable();
    }

    this.store.select(fromAuth.getUser).pipe(
      take(1),
      takeWhile(() => !!this.comment.user),
      map(user => user.id === this.comment.user.id)
    ).subscribe(canEdit => this.canEdit = canEdit);
  }

  remove(): void {
    this.onRemove.emit(CommentForm.to(this.form.value));
  }

  save(): void {
    const value = this.form.value;
    this.onSave.emit(CommentForm.to(value));
    if (this.isNew) {
      this.form.reset({type: 'INTERNAL'});
    } else {
      this.form.disable();
    }
  }

  cancel(): void {
    if (this.originalForm) {
      this.form.patchValue(this.originalForm);
      this.originalForm = undefined;
      this.form.disable();
    } else {
      this.onRemove.emit();
    }
  }

  edit(): void {
    this.form.enable();
    this.originalForm = this.form.value;
  }

  get isNew(): boolean {
    return this.comment ? this.comment.id === undefined : false;
  }

  get formValid(): boolean {
    return this.form.valid && !StringUtil.isEmpty(this.form.value.text);
  }
}
