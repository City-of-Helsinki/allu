import {ChangeDetectionStrategy, Component, Directive, ViewEncapsulation} from '@angular/core';

@Directive({
  selector: '[application-notification-entry]'
})
export class ApplicationNotificationEntryDirective {}

@Component({
  selector: 'application-notification',
  templateUrl: './application-notification.component.html',
  styleUrls: ['./application-notification.component.scss'],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApplicationNotificationComponent {
  constructor() {
  }
}
