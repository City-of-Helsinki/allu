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
  @Input() visibleCount: number;
  @Input() showFull = false;
  @Input() noCommentText: string;

  constructor() {}

  @Input() set comments(comments: Comment[]) {
    this.visibleComments = comments
      ? this.getVisibleComments(comments)
      : [];
  }

  getVisibleComments(allComments: Comment[] = []): Comment[] {
    if (this.visibleCount === undefined || this.visibleCount >= allComments.length) {
      return allComments;
    } else {
      return allComments.slice(0, this.visibleCount);
    }
  }
}
