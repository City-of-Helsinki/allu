import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {InformationRequestSummary} from '@model/information-request/information-request-summary';
import {hasResponse} from '@model/information-request/information-request-status';

@Component({
  selector: 'information-request-summary-pair',
  templateUrl: './information-request-summary-pair.component.html',
  styleUrls: ['./information-request-summary-pair.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InformationRequestSummaryPairComponent {
  @Input() summary: InformationRequestSummary;

  get responseAvailable() {
    return this.summary && hasResponse(this.summary.status);
  }
}
