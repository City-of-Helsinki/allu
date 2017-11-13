import {Component, Input, OnInit} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {Router} from '@angular/router';
import {ApplicationState} from '../../../service/application/application-state';
import {ApplicationHub} from '../../../service/application/application-hub';
import {ApplicationStatus} from '../../../model/application/application-status';
import {ApplicationType} from '../../../model/application/type/application-type';
import {MaterializeUtil} from '../../../util/materialize.util';
import {findTranslation} from '../../../util/translations';
import {NotificationService} from '../../../service/notification/notification.service';
import {Observable} from 'rxjs/Observable';
import {Application} from '../../../model/application/application';
import {Some} from '../../../util/option';

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
  showDelete: boolean = false;
  showCancel: boolean = false;

  constructor(private router: Router,
              private applicationState: ApplicationState,
              private applicationHub: ApplicationHub) {
  }

  ngOnInit(): void {
    this.applicationState.applicationChanges.subscribe(app => {
      let status = app.statusEnum;
      this.showDecision = (ApplicationType[app.type] !== ApplicationType.NOTE) && (status >= ApplicationStatus.HANDLING);
      this.showHandling = status < ApplicationStatus.HANDLING;
      this.showDelete = app.typeEnum === ApplicationType.NOTE;
      this.showCancel = status < ApplicationStatus.DECISION;
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

  moveToHandling(): void {
    this.applicationHub.changeStatus(this.applicationId, ApplicationStatus.HANDLING)
      .subscribe(app => {
        MaterializeUtil.toast(findTranslation('application.statusChange.HANDLING'));
        this.applicationState.application = app;
        this.router.navigate(['/applications', this.applicationId, 'edit']);
    },
    err => NotificationService.errorMessage(findTranslation('application.error.toHandling')));
  }

  toDecisionmaking(): void {
    this.moveToDecisionMaking().subscribe(app => this.router.navigate(['/applications', app.id, 'decision']));
  }

  delete(): void {
    Some(this.applicationId).do(id => this.applicationState.delete(id).subscribe(
      response => {
        NotificationService.message(findTranslation('application.action.deleted'));
        this.router.navigate(['/']);
      },
      error => NotificationService.error(error)));
  }

  cancel(): void {
    if (this.applicationState.application.statusEnum < ApplicationStatus.DECISION) {
      this.applicationHub.changeStatus(this.applicationId, ApplicationStatus.CANCELLED)
        .subscribe(app => {
            MaterializeUtil.toast(findTranslation('application.statusChange.CANCELLED'));
            this.applicationState.application = app;
            this.router.navigate(['/workqueue']);
          },
          err => NotificationService.errorMessage(findTranslation('application.error.cancel')));
    }
  }

  private moveToDecisionMaking(): Observable<Application> {

    if (this.shouldMoveToDecisionMaking()) {
      return this.applicationHub.changeStatus(this.applicationId, ApplicationStatus.DECISIONMAKING)
        .map(app => {
          MaterializeUtil.toast(findTranslation('application.statusChange.DECISIONMAKING'));
          this.applicationState.application = app;
          return app;
        },
        err => NotificationService.errorMessage(findTranslation('application.error.toDecisionmaking')));
    } else {
      return Observable.of(this.applicationState.application);
    }
  }

  private shouldMoveToDecisionMaking(): boolean {
    const appType = this.applicationState.application.typeEnum;
    const status =  this.applicationState.application.statusEnum;
    return appType === ApplicationType.CABLE_REPORT && status === ApplicationStatus.HANDLING;
  }
}
