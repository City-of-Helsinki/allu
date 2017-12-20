import {Component, Input, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {CommentType, decisionProposalComments} from '../../../model/application/comment/comment-type';
import {Comment} from '../../../model/application/comment/comment';
import {TimeUtil} from '../../../util/time.util';
import {ApplicationStore} from '../../../service/application/application-store';

@Component({
  selector: 'decision-proposal',
  templateUrl: './decision-proposal.component.html',
  styleUrls: ['./decision-proposal.component.scss']
})
export class DecisionProposalComponent implements OnInit {
  proposals: Observable<Array<Comment>>;

  constructor(private applicationStore: ApplicationStore) {
  }

  ngOnInit(): void {
    this.proposals = this.applicationStore.comments
      .map(comments => this.sortedProposals(comments));
  }

  private sortedProposals(comments: Array<Comment> = []): Array<Comment> {
    return comments.filter(c => decisionProposalComments.indexOf(CommentType[c.type]) >= 0)
      .sort((l, r) => TimeUtil.compareTo(l.createTime, r.createTime));
  }
}
