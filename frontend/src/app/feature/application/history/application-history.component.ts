import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {MatDialog} from '@angular/material';

import {HistoryHub} from '../../../service/history/history-hub';
import {ApplicationStore} from '../../../service/application/application-store';
import {ApplicationChange} from '../../../model/application/application-change/application-change';
import {UserHub} from '../../../service/user/user-hub';
import {ApplicationHistoryDetailsComponent} from './application-history-details.component';
import {StructureMeta} from '../../../model/application/meta/structure-meta';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {ApplicationHistoryFormatter} from '../../../service/history/application-history-formatter';
import {ArrayUtil} from '../../../util/array-util';
import {MetadataService} from '../../../service/meta/metadata.service';

@Component({
  selector: 'application-history',
  templateUrl: './application-history.component.html',
  styleUrls: [
    './application-history.component.scss'
  ]
})
export class ApplicationHistoryComponent implements OnInit {

  history: Observable<Array<ApplicationChange>>;
  meta: StructureMeta;
  users = new Map<number, string>();

  constructor(private applicationStore: ApplicationStore,
              private metadataService: MetadataService,
              private historyHub: HistoryHub,
              private userHub: UserHub,
              private dialog: MatDialog,
              private notification: NotificationService,
              protected formatter: ApplicationHistoryFormatter) {}

  ngOnInit(): void {
    this.metadataService.loadByApplicationType(this.applicationStore.snapshot.application.type).subscribe(meta => {
      this.meta = meta;
      this.formatter.setMeta(meta);
      this.history = this.historyHub.applicationHistory(this.applicationStore.snapshot.application.id)
        .do(changes => this.fetchUsersForChanges(changes));
    },
    err => this.notification.error(findTranslation('history.error.metadata')));
  }

  showDetails(change: ApplicationChange) {
    this.userHub.getById(change.userId).subscribe(user => {
      const dialogRef = this.dialog.open<ApplicationHistoryDetailsComponent>(ApplicationHistoryDetailsComponent);
      const detailsComponent = dialogRef.componentInstance;
      detailsComponent.change = change;
      detailsComponent.user = user;
      detailsComponent.meta = this.meta;
    });
  }

  fetchUsersForChanges(changes: Array<ApplicationChange>): void {
    const userIds = changes
      .map(c => c.userId)
      .filter(ArrayUtil.unique);

    Observable.combineLatest(userIds.map(id => this.userHub.getById(id)))
      .map(user => user )
      .subscribe(users => users.forEach(user => this.users.set(user.id, user.realName)));
  }
}
