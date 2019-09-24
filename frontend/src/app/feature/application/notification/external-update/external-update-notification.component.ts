import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {InformationRequestModalEvents} from '@feature/information-request/information-request-modal-events';

export enum ExternalUpdateNotificationType {
  INFORMATION_REQUEST_DRAFT = 'INFORMATION_REQUEST_DRAFT',
  INFORMATION_REQUEST_PENDING = 'INFORMATION_REQUEST_PENDING',
  INFORMATION_REQUEST_RESPONSE = 'INFORMATION_REQUEST_RESPONSE',
  PENDING_CLIENT_DATA = 'PENDING_CLIENT_DATA'
}

const informationRequestTypes = [
  ExternalUpdateNotificationType.INFORMATION_REQUEST_DRAFT,
  ExternalUpdateNotificationType.INFORMATION_REQUEST_PENDING
];

@Component({
  selector: 'external-update-notification',
  templateUrl: './external-update-notification.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExternalUpdateNotificationComponent {
  private _type: ExternalUpdateNotificationType = ExternalUpdateNotificationType.INFORMATION_REQUEST_DRAFT;

  constructor(private modalEvents: InformationRequestModalEvents) {
  }

  show(): void {
    if (informationRequestTypes.indexOf(this._type) >= 0) {
      this.modalEvents.openRequest();
    } else {
      this.modalEvents.openAcceptance();
    }
  }

  @Input() set type(type: ExternalUpdateNotificationType) {
    this._type = type;
  }

  get type() {
    return this._type;
  }
}
