import {Component, Input} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {Router} from '@angular/router';
import {ApplicationState} from '../../../service/application/application-state';

@Component({
  selector: 'application-actions',
  viewProviders: [],
  template: require('./application-actions.component.html'),
  styles: []
})
export class ApplicationActionsComponent {

  @Input() readonly = true;
  @Input() applicationId: number;
  @Input() form: FormGroup;
  @Input() submitPending: boolean;
  @Input() showDecision: boolean = true;

  constructor(private router: Router, private applicationState: ApplicationState) {
  }

  copyApplicationAsNew(): void {
    let application = this.applicationState.application;
    application.id = undefined;
    application.attachmentList = [];
    this.applicationState.application = application;
    this.router.navigate(['/applications/edit']);
  }
}
