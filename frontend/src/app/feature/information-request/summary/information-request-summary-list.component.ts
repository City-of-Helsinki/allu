import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {InformationRequestSummary} from '@model/information-request/information-request-summary';

@Component({
  selector: 'information-request-summary-list',
  templateUrl: './information-request-summary-list.component.html',
  styleUrls: ['./information-request-summary-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InformationRequestSummaryListComponent {
  noRequests = true;
  latest: InformationRequestSummary;
  history: InformationRequestSummary[] = [];

  @Input() set summaries(summaries: InformationRequestSummary[]) {
    if (summaries && summaries.length) {
      this.latest = summaries[0];
      this.history = summaries.slice(1, summaries.length);
      this.noRequests = false;
    } else {
      this.history = [];
      this.noRequests = true;
    }
  }
}
