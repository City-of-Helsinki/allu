import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output, QueryList, ViewChildren} from '@angular/core';
import {Comment} from '@model/application/comment/comment';
import {CommentComponent} from './comment.component';

@Component({
  selector: 'comment-list',
  templateUrl: './comment-list.component.html',
  styleUrls: ['./comment-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CommentListComponent {
  @Input() comments: Comment[];
  @Input() loading = false;
  @Output('save') onSave: EventEmitter<Comment> = new EventEmitter<Comment>();
  @Output('remove') onRemove: EventEmitter<Comment> = new EventEmitter<Comment>();

  @ViewChildren('commentRef') children: QueryList<CommentComponent>;

  constructor() {
  }

  save(comment: Comment): void {
    this.onSave.emit(comment);
  }

  remove(comment: Comment): void {
    this.onRemove.emit(comment);
  }

  get dirty(): boolean {
    return this.children ? this.children.some(child => child.form.dirty) : false;
  }
}
