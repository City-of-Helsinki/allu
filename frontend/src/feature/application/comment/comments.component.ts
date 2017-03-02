import {Component, OnInit} from '@angular/core';

import {Application} from '../../../model/application/application';
import {ApplicationState} from '../../../service/application/application-state';
import {Comment} from '../../../model/application/comment/comment';
import {findTranslation} from '../../../util/translations';
import {TimeUtil} from '../../../util/time.util';
import {NotificationService} from '../../../service/notification/notification.service';

@Component({
  selector: 'comments',
  template: require('./comments.component.html'),
  styles: []
})
export class CommentsComponent implements OnInit {
  application: Application;
  comments = [];

  constructor(private applicationState: ApplicationState) {}

  ngOnInit() {
    this.application = this.applicationState.application;
    this.applicationState.comments
      .map(comments => comments.sort((l, r) => TimeUtil.compareTo(r.createTime, l.createTime))) // sort latest first
      .subscribe(
        comments => this.comments = comments,
        err => NotificationService.error(err));
  }

  addNew(): void {
    this.comments = [new Comment].concat(this.comments);
  }

  save(index: number, comment: Comment): void {
    this.applicationState.saveComment(this.application.id, comment).subscribe(c => {
        NotificationService.message(this.translateType(c.type) + ' tallennettu');
        this.comments.splice(index, 1, c);
      },
      error => NotificationService.errorMessage(this.translateType(comment.type) + ' tallentaminen epäonnistui'));
  }

  remove(index: number, comment: Comment): void {
    if (comment.id === undefined) {
      this.comments.splice(index, 1);
    } else {
      this.applicationState.removeComment(comment)
        .subscribe(status => {
            NotificationService.message(this.translateType(comment.type) + ' poistettu');
            this.comments.splice(index, 1);
          },
          error => NotificationService.errorMessage(this.translateType(comment.type) + ' poistaminen epäonnistui'));
    }
  }

  private translateType(commentType: string): string {
    return findTranslation(['comment.type', commentType]);
  }
}
