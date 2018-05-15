import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output, QueryList, ViewChildren} from '@angular/core';
import {TimeUtil} from '../../util/time.util';
import {Comment} from '../../model/application/comment/comment';
import {CommentComponent} from './comment.component';

@Component({
  selector: 'comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CommentsComponent implements OnInit {
  @Input() comments: Comment[];
  @Output('save') onSave: EventEmitter<Comment> = new EventEmitter<Comment>();
  @Output('remove') onRemove: EventEmitter<Comment> = new EventEmitter<Comment>();

  @ViewChildren('commentRef') children: QueryList<CommentComponent>;

  constructor() {
  }

  ngOnInit() {
    this.comments.sort((l, r) => TimeUtil.compareTo(r.createTime, l.createTime)); // sort latest first
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
