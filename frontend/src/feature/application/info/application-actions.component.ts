import {Component, OnDestroy, OnInit, Input} from '@angular/core';

@Component({
  selector: 'application-actions',
  viewProviders: [],
  template: require('./application-actions.component.html'),
  styles: []
})
export class ApplicationActionsComponent {

  @Input() isSummary = true;
  @Input() applicationId: number;
  @Input() formValid: boolean;
  @Input() submitPending: boolean;
}
