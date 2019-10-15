import {ChangeDetectionStrategy, Component, Input} from '@angular/core';

@Component({
  selector: 'validity-start-time',
  templateUrl: './validity-start-time.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ValidityStartTimeComponent {
  @Input() startTime: Date;
  @Input() recurring = false;
}
