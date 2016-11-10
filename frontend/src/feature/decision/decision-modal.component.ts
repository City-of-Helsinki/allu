import {Component, Input, Output, EventEmitter} from '@angular/core';
import {ApplicationStatusChange, ApplicationStatus, translateStatus} from '../../model/application/application-status-change';
import {MaterializeAction} from 'angular2-materialize';

@Component({
  selector: 'decision-modal',
  template: require('./decision-modal.component.html'),
  styles: [require('./decision-modal.component.scss')]
})
export class DecisionModalComponent {
  @Input() status: string;
  @Input() header: string;
  @Input() confirmText: string;
  @Output() statusChange = new EventEmitter<ApplicationStatusChange>();
  modalActions = new EventEmitter<string|MaterializeAction>();

  public comment: string;

  confirm() {
    this.statusChange.emit(ApplicationStatusChange.withComment(undefined, ApplicationStatus[this.status], this.comment));
  }

  openModal() {
    this.modalActions.emit({action: 'modal', params: ['open']});
  }
  closeModal() {
    this.modalActions.emit({action: 'modal', params: ['close']});
  }
}
