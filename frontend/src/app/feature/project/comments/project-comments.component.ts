import {Component, ViewChild} from '@angular/core';
import {CanComponentDeactivate} from '../../../service/common/can-deactivate-guard';
import {CommentsComponent} from '../../comment/comments.component';
import {Observable} from 'rxjs/Observable';

@Component({
  selector: 'project-comments',
  template: '<comments targetType="Project"></comments>',
  styleUrls: []
})
export class ProjectCommentsComponent implements CanComponentDeactivate {
  @ViewChild(CommentsComponent) commentsComponent;

  canDeactivate(): Observable<boolean> | boolean {
    return this.commentsComponent.canDeactivate();
  }
}
