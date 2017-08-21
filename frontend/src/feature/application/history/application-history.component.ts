import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {MdDialog} from '@angular/material';

import {HistoryHub} from '../../../service/history/history-hub';
import {ApplicationState} from '../../../service/application/application-state';
import {ApplicationChange} from '../../../model/application/application-change/application-change';
import {UserHub} from '../../../service/user/user-hub';
import {User} from '../../../model/common/user';
import {ApplicationHistoryDetailsComponent} from './application-history-details.component';
import {ApplicationHub} from '../../../service/application/application-hub';
import {StructureMeta} from '../../../model/application/meta/structure-meta';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {ApplicationHistoryFormatter} from '../../../service/history/application-history-formatter';

@Component({
  selector: 'application-history',
  template: require('./application-history.component.html'),
  styles: [
    require('./application-history.component.scss')
  ]
})
export class ApplicationHistoryComponent implements OnInit {

  history: Observable<Array<ApplicationChange>>;
  handlers = new Map<number, User>();
  meta: StructureMeta;

  constructor(private applicationState: ApplicationState,
              private applicationHub: ApplicationHub,
              private historyHub: HistoryHub,
              private userHub: UserHub,
              private dialog: MdDialog,
              protected formatter: ApplicationHistoryFormatter) {}

  ngOnInit(): void {
    this.applicationHub.loadMetaData(this.applicationState.application.type).subscribe(meta => {
      this.meta = meta;
      this.formatter.setMeta(meta);
      this.history = this.historyHub.applicationHistory(this.applicationState.application.id);
      this.userHub.getActiveUsers().subscribe(users => users.forEach(user => this.handlers.set(user.id, user)));
    },
    err => NotificationService.errorMessage(findTranslation('history.error.metadata')));
  }

  showDetails(change: ApplicationChange) {
    let dialogRef = this.dialog.open(ApplicationHistoryDetailsComponent);
    let detailsComponent = dialogRef.componentInstance;
    detailsComponent.change = change;
    detailsComponent.user = this.handlers.get(change.userId);
    detailsComponent.meta = this.meta;
  }
}
