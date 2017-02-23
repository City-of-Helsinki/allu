import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {MdDialog} from '@angular/material';

import {HistoryHub} from '../../../service/history/history-hub';
import {ApplicationState} from '../../../service/application/application-state';
import {ApplicationChange} from '../../../model/application/application-change/application-change';
import {UserHub} from '../../../service/user/user-hub';
import {User} from '../../../model/common/user';
import {ApplicationHistoryDetailsComponent} from './application-history-details.component';

@Component({
  selector: 'application-history',
  template: require('./application-history.component.html'),
  styles: []
})
export class ApplicationHistoryComponent implements OnInit {

  history: Observable<Array<ApplicationChange>>;
  handlers = new Map<number, User>();

  constructor(private applicationState: ApplicationState,
              private historyHub: HistoryHub,
              private userHub: UserHub,
              private dialog: MdDialog) {}

  ngOnInit(): void {
    this.history = this.historyHub.applicationHistory(this.applicationState.application.id);
    this.userHub.getActiveUsers().subscribe(users => users.forEach(user => this.handlers.set(user.id, user)));
  }

  showDetails(change: ApplicationChange) {
    let dialogRef = this.dialog.open(ApplicationHistoryDetailsComponent);
    let detailsComponent = dialogRef.componentInstance;
    detailsComponent.change = change;
    detailsComponent.user = this.handlers.get(change.userId);
  }
}
