import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {InformationRequestSummary} from '@model/information-request/information-request-summary';
import {canHaveResponse, InformationRequestStatus} from '@model/information-request/information-request-status';

@Component({
  selector: 'information-request-summary-pair',
  templateUrl: './information-request-summary-pair.component.html',
  styleUrls: ['./information-request-summary-pair.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InformationRequestSummaryPairComponent {
  @Input() summary: InformationRequestSummary;

  get responseAvailable() {
    return this.summary && canHaveResponse(this.summary.status);
  }

  get connectionClass() {
    if (!this.summary || InformationRequestStatus.CLOSED === this.summary.status) {
      return 'connection-closed';
    } else {
      return 'connection-active';
    }
  }
}
