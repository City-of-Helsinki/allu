import {Component, Input, ViewChildren, QueryList} from '@angular/core';

import {Observable} from 'rxjs/Observable';
import '../../../rxjs-extensions.ts';
import {MdButton} from '@angular2-material/button';

import {Application} from '../../../model/application/application';
import {ApplicationHub} from '../../../service/application-hub';
import {ApplicationStatusChange, ApplicationStatus} from '../../../model/application/application-status-change';
import {DecisionModalComponent} from './decision-modal.component';
import {MaterializeDirective} from 'angular2-materialize';

@Component({
  selector: 'decision-actions',
  moduleId: module.id,
  template: require('./decision-actions.component.html'),
  styles: [],
  directives: [
    MdButton,
    MaterializeDirective,
    DecisionModalComponent
  ]
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
