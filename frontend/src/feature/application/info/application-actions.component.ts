import {Component, Input, OnInit} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {Router} from '@angular/router';
import {ApplicationState} from '../../../service/application/application-state';
import {ApplicationHub} from '../../../service/application/application-hub';
import {ApplicationStatusChange} from '../../../model/application/application-status-change';
import {ApplicationStatus} from '../../../model/application/application-status';
import {ApplicationType} from '../../../model/application/type/application-type';
import {MaterializeUtil} from '../../../util/materialize.util';
import {findTranslation} from '../../../util/translations';
import {NotificationService} from '../../../service/notification/notification.service';

@Component({
  selector: 'application-actions',
  viewProviders: [],
  template: require('./application-actions.component.html'),
  styles: [
    require('./application-actions.component.scss')
  ]
})
export class ApplicationActionsComponent implements OnInit {

  @Input() readonly = true;
  @Input() applicationId: number;
  @Input() form: FormGroup;
  @Input() status: string;
  @Input() submitPending: boolean;

  showDecision: boolean = true;
  showHandling: boolean = true;

  constructor(private router: Router,
              private applicationState: ApplicationState,
              private applicationHub: ApplicationHub) {
  }

  ngOnInit(): void {
    this.applicationState.applicationChanges.subscribe(app => {
      this.showDecision = ApplicationType[app.type] !== ApplicationType.NOTE;
      this.showHandling = ApplicationStatus[app.status] !== ApplicationStatus.HANDLING;
    });
  }

  copyApplicationAsNew(): void {
    let application = this.applicationState.application;
    application.id = undefined;
    application.attachmentList = [];
    application.locations = application.locations.map(loc => loc.copyAsNew());
    this.applicationState.applicationCopy = application;
    this.router.navigate(['/applications/edit']);
  }

  legalChange(newStatus: string): boolean {
    return ApplicationStatusChange.legalChange(this.applicationState.application.status, newStatus);
  }

  moveToHandling(): void {
    this.applicationHub.changeStatus(new ApplicationStatusChange(this.applicationId, ApplicationStatus.HANDLING)).
      subscribe(app => {
        MaterializeUtil.toast(findTranslation('application.statusChange.HANDLING'));
        this.applicationState.application = app;
        this.router.navigate(['/applications', this.applicationId, 'edit']);
    },
    err => NotificationService.errorMessage(findTranslation('application.error.toHandling')));
  }

  moveToDecisionmaking(): void {
    this.applicationHub.changeStatus(new ApplicationStatusChange(this.applicationId, ApplicationStatus.DECISIONMAKING)).
    subscribe(app => {
        MaterializeUtil.toast(findTranslation('application.statusChange.DECISIONMAKING'));
        this.applicationState.application = app;
        this.router.navigate(['/decision', this.applicationId]);
      },
      err => NotificationService.errorMessage(findTranslation('application.error.toDecisionmaking')));
  }
}
