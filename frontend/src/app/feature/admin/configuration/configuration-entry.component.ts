import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Configuration} from '@model/config/configuration';

@Component({
  selector: 'configuration-entry',
  templateUrl: './configuration-entry.component.html',
  styleUrls: ['./configuration-entry.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfigurationEntryComponent {
  @Input() configuration: Configuration;
}

