import {Component, Input} from '@angular/core';

import {Application} from '../../model/application/application';
import {ApplicationHub} from '../../service/application/application-hub';
import {ApplicationStatusChange, ApplicationStatus} from '../../model/application/application-status-change';

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
    this.applicationHub.changeStatus(confirm).subscribe(application => this.statusChanged(application));
  }

  public accept() {
    console.log('accept', this.application.id);
    this.applicationHub.changeStatus(ApplicationStatusChange.of(this.application.id, ApplicationStatus.DECISION))
      .subscribe(application => this.statusChanged(application));
  }

  private statusChanged(application: Application): void {
    console.log('Status changed to', application.status);
    this.application = application;
  }
}
