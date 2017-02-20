import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {HistoryHub} from '../../../service/history/history-hub';
import {ApplicationState} from '../../../service/application/application-state';
import {ApplicationChange} from '../../../model/application/application-change/application-change';
import {UserHub} from '../../../service/user/user-hub';

@Component({
  selector: 'application-history',
  template: require('./application-history.component.html'),
  styles: []
})
export class ApplicationHistoryComponent implements OnInit {

  history: Observable<Array<ApplicationChange>>;
  handlers = new Map<number, string>();

  constructor(private applicationState: ApplicationState,
              private historyHub: HistoryHub,
              private userHub: UserHub) {}

  ngOnInit(): void {
    this.history = this.historyHub.applicationHistory(this.applicationState.application.id);
    this.userHub.getActiveUsers().subscribe(users => users.forEach(user => this.handlers.set(user.id, user.realName)));
  }
}
