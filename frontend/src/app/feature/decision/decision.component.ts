import {Component, OnInit} from '@angular/core';
import {Application} from '@model/application/application';
import {ApplicationStore} from '@service/application/application-store';
import {Observable} from 'rxjs';
import {AttachmentInfo} from '@model/application/attachment/attachment-info';
import {map} from 'rxjs/operators';
import {select, Store} from '@ngrx/store';
import * as fromApplication from '@feature/application/reducers';
import {DistributionEntry} from '@model/common/distribution-entry';

@Component({
  selector: 'decision',
  templateUrl: './decision.component.html',
  styleUrls: ['./decision.component.scss']
})
export class DecisionComponent implements OnInit {
  applicationChanges$: Observable<Application>;
  decisionAttachments$: Observable<Array<AttachmentInfo>>;
  distributionList$: Observable<DistributionEntry[]>;

  constructor(
    private applicationStore: ApplicationStore,
    private store: Store<fromApplication.State>) {}

  ngOnInit(): void {
    this.applicationChanges$ = this.store.pipe(select(fromApplication.getCurrentApplication));
    this.decisionAttachments$ = this.applicationStore.attachments.pipe(
      map(attachments => attachments.filter(a => a.decisionAttachment))
    );
    this.distributionList$ = this.store.pipe(
      select(fromApplication.getCurrentApplication),
      map(app => app.decisionDistributionList)
    );
  }
}
