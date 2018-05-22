import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Comment} from '../../model/application/comment/comment';

@Component({
  selector: 'comment-preview-list',
  templateUrl: './comment-preview-list.component.html',
  styleUrls: ['./comment-preview-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CommentPreviewListComponent {
  visibleComments: Comment[];
  @Input() commentLink: (string | number)[];

  constructor() {}

  @Input('comments') set comments(comments: Comment[]) {
    this.visibleComments = comments
      ? comments.slice(0, 3) // Take only 3 first comments
      : [];
  }
}
