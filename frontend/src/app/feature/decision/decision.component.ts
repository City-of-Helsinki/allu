import {Component, OnInit} from '@angular/core';
import {Application} from '@model/application/application';
import {ApplicationStore} from '@service/application/application-store';
import {Observable} from 'rxjs';
import {AttachmentInfo} from '@model/application/attachment/attachment-info';
import {map, switchMap, take} from 'rxjs/operators';
import {select, Store} from '@ngrx/store';
import * as fromApplication from '@feature/application/reducers';
import {DistributionEntry} from '@model/common/distribution-entry';
import { MatDialog } from '@angular/material/dialog';
import {DISTRIBUTION_MODAL_CONFIG, DistributionModalComponent} from '@feature/application/distribution/distribution-modal.component';
import {distributionChangeAllowed} from '@model/application/application-status';

@Component({
  selector: 'decision',
  templateUrl: './decision.component.html',
  styleUrls: ['./decision.component.scss']
})
export class DecisionComponent implements OnInit {
  applicationChanges$: Observable<Application>;
  decisionAttachments$: Observable<Array<AttachmentInfo>>;
  distributionList$: Observable<DistributionEntry[]>;
  distributionChangeAllowed$: Observable<boolean>;

  constructor(
    private applicationStore: ApplicationStore,
    private store: Store<fromApplication.State>,
    private dialog: MatDialog) {}

  ngOnInit(): void {
    this.applicationChanges$ = this.store.pipe(select(fromApplication.getCurrentApplication));

    this.decisionAttachments$ = this.applicationStore.attachments.pipe(
      map(attachments => attachments.filter(a => a.decisionAttachment))
    );

    this.distributionList$ = this.store.pipe(select(fromApplication.getDistributionList));

    this.distributionChangeAllowed$ = this.store.pipe(
      select(fromApplication.getCurrentApplication),
      map(app => distributionChangeAllowed(app.status))
    );
  }

  editDistribution(): void {
    this.store.pipe(
      select(fromApplication.getDistributionList),
      take(1),
      map(distribution => this.createModalConfig(distribution)),
      switchMap(config => this.dialog.open<DistributionModalComponent>(DistributionModalComponent, config).afterClosed())
    ).subscribe(() => {});
  }

  private createModalConfig(distribution: DistributionEntry[]) {
    return {
      ...DISTRIBUTION_MODAL_CONFIG,
      data: { distribution }
    };
  }
}
