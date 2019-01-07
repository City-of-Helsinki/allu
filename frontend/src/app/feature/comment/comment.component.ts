import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {manualCommentNames} from '@model/application/comment/comment-type';
import {CommentForm} from './comment-form';
import {Comment} from '@model/application/comment/comment';
import {StringUtil} from '@util/string.util';
import {take} from 'rxjs/internal/operators';
import {combineLatest} from 'rxjs';
import {CurrentUser} from '@service/user/current-user';

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

  constructor(private fb: FormBuilder,
              private currentUser: CurrentUser) {
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

    this.currentUserCanEdit(this.comment.user ? this.comment.user.id : undefined);
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
      this.form.markAsPristine();
      this.form.disable();
    }
  }

  cancel(): void {
    if (this.originalForm) {
      this.form.patchValue(this.originalForm);
      this.originalForm = undefined;
      this.form.disable();
      this.form.markAsPristine();
    } else {
      this.onRemove.emit();
    }
  }

  private currentUserCanEdit(creatorId: number): void {
    combineLatest(
        this.currentUser.isCurrentUser(creatorId),
        this.currentUser.hasRole(['ROLE_CREATE_APPLICATION', 'ROLE_PROCESS_APPLICATION', 'ROLE_DECISION', 'ROLE_DECLARANT']),
        this.currentUser.hasRole(['ROLE_ADMIN'])
      ).pipe(
        take(1)
      ).subscribe(([isCurrent, hasRole, isAdmin]) => {
        this.canEdit = ((creatorId === undefined || isCurrent) && hasRole) || isAdmin;
      }
    );
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
