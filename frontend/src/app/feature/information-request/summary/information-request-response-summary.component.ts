import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {InformationRequestSummary} from '@model/information-request/information-request-summary';

@Component({
  selector: 'information-request-response-summary',
  templateUrl: './information-request-response-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InformationRequestResponseSummaryComponent {
  @Input() summary: InformationRequestSummary;
}
