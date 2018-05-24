import {Component, ViewChild} from '@angular/core';
import {CanComponentDeactivate} from '../../../service/common/can-deactivate-guard';
import {CommentsComponent} from '../../comment/comments.component';
import {Observable} from 'rxjs';

@Component({
  selector: 'application-comments',
  template: '<comments targetType="Application"></comments>',
  styleUrls: []
})
export class ApplicationCommentsComponent implements CanComponentDeactivate {
  @ViewChild(CommentsComponent) commentsComponent;

  canDeactivate(): Observable<boolean> | boolean {
    return this.commentsComponent.canDeactivate();
  }
}
