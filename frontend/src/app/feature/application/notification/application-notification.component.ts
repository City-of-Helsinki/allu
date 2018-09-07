import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {InformationRequestModalEvents} from '@feature/information-request/information-request-modal-events';

export enum ApplicationNotificationType {
  INFORMATION_REQUEST_DRAFT = 'INFORMATION_REQUEST_DRAFT',
  INFORMATION_REQUEST_RESPONSE = 'INFORMATION_REQUEST_RESPONSE',
  PENDING_CLIENT_DATA = 'PENDING_CLIENT_DATA'
}

@Component({
  selector: 'application-notification',
  templateUrl: './application-notification.component.html',
  styleUrls: ['./application-notification.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApplicationNotificationComponent {
  private _type: ApplicationNotificationType = ApplicationNotificationType.INFORMATION_REQUEST_DRAFT;

  constructor(private modalEvents: InformationRequestModalEvents) {
  }

  show(): void {
    if (ApplicationNotificationType.INFORMATION_REQUEST_DRAFT === this._type) {
      this.modalEvents.openRequest();
    } else {
      this.modalEvents.openAcceptance();
    }
  }

  @Input() set type(type: ApplicationNotificationType) {
    this._type = type;
  }

  get type() {
    return this._type;
  }
}
