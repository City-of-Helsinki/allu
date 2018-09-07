import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {InformationAcceptanceModalEvents} from '@feature/information-request/acceptance/information-acceptance-modal-events';

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
  @Input() type: ApplicationNotificationType = ApplicationNotificationType.INFORMATION_REQUEST_DRAFT;

  constructor(private modalEvents: InformationAcceptanceModalEvents) {
  }

  show(): void {
    this.modalEvents.open();
  }
}
