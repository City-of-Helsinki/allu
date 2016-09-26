import {Component, Input, ViewChildren, QueryList} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import '../../../rxjs-extensions.ts';

import {Application} from '../../../model/application/application';
import {ApplicationHub} from '../../../service/application/application-hub';
import {ApplicationStatusChange, ApplicationStatus} from '../../../model/application/application-status-change';

@Component({
  selector: 'decision-actions',
  template: require('./decision-actions.component.html'),
  styles: []
})
export class DecisionActionsComponent {
  @Input() application: Application;

  constructor(private applicationHub: ApplicationHub) {}

  public decisionConfirmed(confirm: ApplicationStatusChange) {
    confirm.id = this.application.id;
    this.applicationHub.addApplicationStatusChange(confirm);
  }

  public accept() {
    this.applicationHub.addApplicationStatusChange(ApplicationStatusChange.of(this.application.id, ApplicationStatus.DECISION));
  }
}
