import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {InformationRequestSummary} from '@model/information-request/information-request-summary';

@Component({
  selector: 'information-request-summary-list',
  templateUrl: './information-request-summary-list.component.html',
  styleUrls: ['./information-request-summary-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InformationRequestSummaryListComponent {
  @Input() summaries: InformationRequestSummary[] = [];
}
