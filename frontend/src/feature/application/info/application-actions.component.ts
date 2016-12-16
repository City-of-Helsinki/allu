import {Component, OnDestroy, OnInit, Input} from '@angular/core';
import {FormGroup} from '@angular/forms';

@Component({
  selector: 'application-actions',
  viewProviders: [],
  template: require('./application-actions.component.html'),
  styles: []
})
export class ApplicationActionsComponent {

  @Input() isSummary = true;
  @Input() applicationId: number;
  @Input() form: FormGroup;
  @Input() submitPending: boolean;
}
