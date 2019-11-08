import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {InformationRequestField} from '@model/information-request/information-request-field';

export type InformationRequestType = 'request' | 'response';

@Component({
  selector: 'information-request-summary-fields',
  templateUrl: './information-request-summary-fields.component.html',
  styleUrls: ['./information-request-summary-fields.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InformationRequestSummaryFieldsComponent {
  @Input() type: InformationRequestType = 'request';
  @Input() columns: string[];
  @Input() fields: InformationRequestField[];
}
