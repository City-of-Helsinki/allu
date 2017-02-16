import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';

import {Application} from '../../model/application/application';
import {ApplicationHub} from '../../service/application/application-hub';
import {ApplicationStatusChange} from '../../model/application/application-status-change';
import {ApplicationStatus} from '../../model/application/application-status';
import {MaterializeUtil} from '../../util/materialize.util';
import {translations} from '../../util/translations';

@Component({
  selector: 'decision-actions',
  template: require('./decision-actions.component.html'),
  styles: [require('./decision-actions.component.scss')]
})
export class DecisionActionsComponent {
  @Input() application: Application;

  private translations = translations;

  constructor(private applicationHub: ApplicationHub, private router: Router) {}

  public decisionConfirmed(confirm: ApplicationStatusChange) {
    confirm.id = this.application.id;
    this.applicationHub.changeStatus(confirm).subscribe(application => this.statusChanged(application));
  }

  public accept() {
    this.applicationHub.changeStatus(ApplicationStatusChange.of(this.application.id, ApplicationStatus.DECISION))
      .subscribe(application => this.statusChanged(application));
  }

  private statusChanged(application: Application): void {
    this.application = application;
    MaterializeUtil.toast(translations.decision.type[application.status], 4000);
    this.router.navigateByUrl('/workqueue');
  }
}
