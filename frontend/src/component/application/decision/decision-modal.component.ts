import {Component, Input, Output, EventEmitter} from '@angular/core';
import {MaterializeDirective} from 'angular2-materialize';
import {ApplicationStatusChange, ApplicationStatus, translateStatus} from '../../../model/application/application-status-change';

@Component({
  selector: 'decision-modal',
  template: require('./decision-modal.component.html'),
  styles: [],
  directives: [
    MaterializeDirective
  ]
})
export class DecisionModalComponent {
  @Input() status: string;
  @Input() header: string;
  @Input() confirmText: string;
  @Output() statusChange = new EventEmitter<ApplicationStatusChange>();

  public comment: string;

  confirm() {
    this.statusChange.emit(ApplicationStatusChange.withComment(undefined, ApplicationStatus[this.status], this.comment));
  }
}
