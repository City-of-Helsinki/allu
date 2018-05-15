import {Component, Input, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {CommentType, decisionProposalComments} from '../../../model/application/comment/comment-type';
import {Comment} from '../../../model/application/comment/comment';
import {TimeUtil} from '../../../util/time.util';
import {Store} from '@ngrx/store';
import * as fromApplication from '../../application/reducers';

@Component({
  selector: 'decision-proposal',
  templateUrl: './decision-proposal.component.html',
  styleUrls: ['./decision-proposal.component.scss']
})
export class DecisionProposalComponent implements OnInit {
  proposals: Observable<Array<Comment>>;

  constructor(private store: Store<fromApplication.State>) {
  }

  ngOnInit(): void {
    this.proposals = this.store.select(fromApplication.getAllComments)
      .map(comments => this.sortedProposals(comments));
  }

  private sortedProposals(comments: Array<Comment> = []): Array<Comment> {
    return comments.filter(c => decisionProposalComments.indexOf(CommentType[c.type]) >= 0)
      .sort((l, r) => TimeUtil.compareTo(l.createTime, r.createTime));
  }
}
