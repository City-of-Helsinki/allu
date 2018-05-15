import {Component, Input, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {CommentType} from '../../../model/application/comment/comment-type';
import {Comment} from '../../../model/application/comment/comment';
import {CommentService} from '../../../service/application/comment/comment.service';

@Component({
  selector: 'invoice-comments',
  templateUrl: './invoice-comments.component.html',
  styleUrls: [
    './invoice-comments.component.scss'
  ]
})
export class InvoiceCommentsComponent implements OnInit {
  @Input() applicationId: number;
  comments: Observable<Array<Comment>>;

  constructor(private commentService: CommentService) {
  }

  ngOnInit(): void {
    this.comments = this.getInvoicingComments();
  }

  private getInvoicingComments(): Observable<Array<Comment>> {
    return this.commentService.getComments(this.applicationId)
      .map(comments => comments.filter(c => CommentType[c.type] === CommentType.INVOICING));
  }
}
