import {ChangeDetectionStrategy, Component, HostBinding, Input} from '@angular/core';

@Component({
  selector: 'field-display',
  templateUrl: './field-display.component.html',
  styleUrls: ['./field-display.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FieldDisplayComponent {
  @Input() label: string;
  @Input() value: any;

  @HostBinding('class') classNames = 'field-display';
}
