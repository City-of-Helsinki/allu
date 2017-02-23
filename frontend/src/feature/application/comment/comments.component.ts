import {Component, OnInit} from '@angular/core';

import {Application} from '../../../model/application/application';
import {MaterializeUtil} from '../../../util/materialize.util';
import {ApplicationState} from '../../../service/application/application-state';
import {Comment} from '../../../model/application/comment/comment';
import {findTranslation} from '../../../util/translations';
import {CommentHub} from '../../../service/application/comment/comment-hub';
import {TimeUtil} from '../../../util/time.util';

const toastTime = 4000;

@Component({
  selector: 'comments',
  template: require('./comments.component.html'),
  styles: []
})
export class CommentsComponent implements OnInit {
  application: Application;
  comments = [];

  constructor(private applicationState: ApplicationState, private commentHub: CommentHub) {}

  ngOnInit() {
    this.application = this.applicationState.application;
    this.applicationState.comments
      .map(comments => comments.sort((l, r) => TimeUtil.compareTo(r.createTime, l.createTime))) // sort latest first
      .subscribe(comments => this.comments = comments);
  }

  addNew(): void {
    this.comments = [new Comment].concat(this.comments);
  }

  save(index: number, comment: Comment): void {
    this.applicationState.saveComment(this.application.id, comment).subscribe(c => {
      MaterializeUtil.toast(this.translateType(c.type) + ' tallennettu', toastTime);
      this.comments.splice(index, 1, c);
    },
    error => {
      MaterializeUtil.toast(this.translateType(comment.type) + ' tallentaminen epäonnistui', toastTime);
    });
  }

  remove(index: number, comment: Comment): void {
    if (comment.id === undefined) {
      this.comments.splice(index, 1);
    } else {
      this.applicationState.removeComment(comment)
        .subscribe(status => {
            MaterializeUtil.toast(this.translateType(comment.type) + ' poistettu', toastTime);
            this.comments.splice(index, 1);
          },
          error => MaterializeUtil.toast(this.translateType(comment.type) + ' poistaminen epäonnistui', toastTime));
    }
  }

  private translateType(commentType: string): string {
    return findTranslation(['comment.type', commentType]);
  }
}
