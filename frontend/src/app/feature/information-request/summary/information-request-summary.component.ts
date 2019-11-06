import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {InformationRequestSummary} from '@model/information-request/information-request-summary';

@Component({
  selector: 'information-request-summary',
  templateUrl: './information-request-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InformationRequestSummaryComponent {
  @Input() summary: InformationRequestSummary;
}
