import {Component, Input, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {InvoiceRow} from '../../../model/application/invoice/invoice-row';
import {InvoiceHub} from '../../../service/application/invoice/invoice-hub';
import {CommentHub} from '../../../service/application/comment/comment-hub';
import {Observable} from 'rxjs/Observable';
import {CommentType} from '../../../model/application/comment/comment-type';
import {Comment} from '../../../model/application/comment/comment';

@Component({
  selector: 'invoice-comments',
  template: require('./invoice-comments.component.html'),
  styles: [
    require('./invoice-comments.component.scss')
  ]
})
export class InvoiceCommentsComponent implements OnInit {
  @Input() applicationId: number;
  comments: Observable<Array<Comment>>;

  constructor(private commentHub: CommentHub) {
  }

  ngOnInit(): void {
    this.comments = this.getInvoicingComments();
  }

  private getInvoicingComments(): Observable<Array<Comment>> {
    return this.commentHub.getComments(this.applicationId)
      .map(comments => comments.filter(c => CommentType[c.type] === CommentType.INVOICING));
  }
}
