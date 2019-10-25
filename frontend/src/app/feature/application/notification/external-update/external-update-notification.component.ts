import {ChangeDetectionStrategy, Component, Input} from '@angular/core';

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
  routePath = 'pending_info';
  private _type: ExternalUpdateNotificationType = ExternalUpdateNotificationType.INFORMATION_REQUEST_DRAFT;

  constructor() {}

  show(): void {
  }

  @Input() set type(type: ExternalUpdateNotificationType) {
    this._type = type;
    if (informationRequestTypes.indexOf(this._type) >= 0) {
      this.routePath = 'information_request';
    } else {
      this.routePath = 'pending_info';
    }
  }

  get type() {
    return this._type;
  }
}
