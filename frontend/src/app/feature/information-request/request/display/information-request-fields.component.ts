import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {InformationRequestField} from '@model/information-request/information-request-field';

@Component({
  selector: 'information-request-fields',
  templateUrl: './information-request-fields.component.html',
  styleUrls: ['./information-request-fields.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InformationRequestFieldsComponent {
  @Input() requestFields: InformationRequestField[];
}
