import {Component, OnInit, OnDestroy} from '@angular/core';
import {MatDialog} from '@angular/material';
import {FormArray, FormBuilder, Validators} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {Subscription} from 'rxjs/Subscription';

import {Application} from '../../../model/application/application';
import {ApplicationStore} from '../../../service/application/application-store';
import {Comment} from '../../../model/application/comment/comment';
import {findTranslation} from '../../../util/translations';
import {TimeUtil} from '../../../util/time.util';
import {NotificationService} from '../../../service/notification/notification.service';
import {CanComponentDeactivate} from '../../../service/common/can-deactivate-guard';
import {ConfirmDialogComponent} from '../../common/confirm-dialog/confirm-dialog.component';
import {CommentForm} from './comment-form';
import {FormUtil} from '../../../util/form.util';

@Component({
  selector: 'comments',
  templateUrl: './comments.component.html',
  styleUrls: []
})
export class CommentsComponent implements OnInit, OnDestroy, CanComponentDeactivate {
  application: Application;
  commentSubsciption: Subscription;
  comments: FormArray;

  constructor(private applicationStore: ApplicationStore,
              private dialog: MatDialog,
              private fb: FormBuilder) {
    this.comments = this.fb.array([]);
  }

  ngOnInit() {
    this.application = this.applicationStore.snapshot.application;
    this.commentSubsciption = this.applicationStore.comments
      .map(comments => comments.sort((l, r) => TimeUtil.compareTo(r.createTime, l.createTime))) // sort latest first
      .subscribe(comments => {
        FormUtil.clearArray(this.comments);
        comments.forEach(comment => this.addNew(comment));
      }, err => NotificationService.error(err));
  }

  ngOnDestroy(): void {
    this.commentSubsciption.unsubscribe();
  }

  addNew(comment: Comment = new Comment()): void {
    const formGroup = this.fb.group({
      id: [undefined],
      type: [undefined],
      text: [undefined, Validators.required],
      createTime: [undefined],
      updateTime: [undefined],
      user: [undefined, Validators.required],
    });
    formGroup.patchValue(CommentForm.from(comment));

    if (comment.id === undefined) {
      this.comments.insert(0, formGroup);
    } else {
      this.comments.push(formGroup);
    }
  }

  save(index: number, comment: Comment): void {
    this.applicationStore.saveComment(this.application.id, comment).subscribe(c => {
        NotificationService.message(this.translateType(c.type) + ' tallennettu');
      },
      error => NotificationService.errorMessage(this.translateType(comment.type) + ' tallentaminen epäonnistui'));
  }

  remove(index: number, comment: Comment): void {
    if (comment.id === undefined) {
      this.comments.removeAt(index);
    } else {
      this.applicationStore.removeComment(comment.id)
        .subscribe(status => {
            NotificationService.message(this.translateType(comment.type) + ' poistettu');
            this.comments.removeAt(index);
          },
          error => NotificationService.errorMessage(this.translateType(comment.type) + ' poistaminen epäonnistui'));
    }
  }

  canDeactivate(): Observable<boolean> | boolean {
    if (this.comments.dirty) {
      return this.confirmChanges();
    } else {
      return true;
    }
  }

  private confirmChanges(): Observable<boolean> {
    const data = {
      title: findTranslation(['comment.confirmDiscard.title']),
      description: findTranslation(['comment.confirmDiscard.description']),
      confirmText: findTranslation(['comment.confirmDiscard.confirmText']),
      cancelText: findTranslation(['comment.confirmDiscard.cancelText'])
    };
    return this.dialog.open(ConfirmDialogComponent, {data}).afterClosed();
  }

  private translateType(commentType: string): string {
    return findTranslation(['comment.type', commentType]);
  }
}
