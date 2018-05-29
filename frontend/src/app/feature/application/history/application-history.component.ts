import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {MatDialog} from '@angular/material';

import {HistoryHub} from '../../../service/history/history-hub';
import {ApplicationStore} from '../../../service/application/application-store';
import {ChangeHistoryItem} from '../../../model/history/change-history-item';
import {UserHub} from '../../../service/user/user-hub';
import {ApplicationHistoryDetailsComponent} from './application-history-details.component';
import {StructureMeta} from '../../../model/application/meta/structure-meta';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {ApplicationHistoryFormatter} from '../../../service/history/application-history-formatter';
import {MetadataService} from '../../../service/meta/metadata.service';

@Component({
  selector: 'application-history',
  templateUrl: './application-history.component.html',
  styleUrls: [
    './application-history.component.scss'
  ]
})
export class ApplicationHistoryComponent implements OnInit {

  history: Observable<Array<ChangeHistoryItem>>;
  meta: StructureMeta;

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
      this.history = this.historyHub.applicationHistory(this.applicationStore.snapshot.application.id);
    },
    err => this.notification.error(findTranslation('history.error.metadata')));
  }

  showDetails(change: ChangeHistoryItem) {
    const dialogRef = this.dialog.open<ApplicationHistoryDetailsComponent>(ApplicationHistoryDetailsComponent);
    const detailsComponent = dialogRef.componentInstance;
    detailsComponent.change = change;
    detailsComponent.user = change.user;
    detailsComponent.meta = this.meta;
  }
}
