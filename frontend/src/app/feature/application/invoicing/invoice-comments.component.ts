import {Component, Input, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {CommentType} from '../../../model/application/comment/comment-type';
import {Comment} from '../../../model/application/comment/comment';
import * as fromApplication from '../reducers';
import {Store} from '@ngrx/store';
import {map} from 'rxjs/internal/operators';

@Component({
  selector: 'invoice-comments',
  templateUrl: './invoice-comments.component.html',
  styleUrls: [
    './invoice-comments.component.scss'
  ]
})
export class InvoiceCommentsComponent implements OnInit {
  @Input() applicationId: number;
  comments$: Observable<Array<Comment>>;

  constructor(private store: Store<fromApplication.State>) {
  }

  ngOnInit(): void {
    this.comments$ = this.store.select(fromApplication.getAllComments).pipe(
      map(comments => comments.filter(c => c.type === CommentType.INVOICING))
    );
  }
}
