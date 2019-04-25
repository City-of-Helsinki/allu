import {Component, Input} from '@angular/core';
import {InformationRequest} from '@model/information-request/information-request';

@Component({
  selector: 'information-request-summary',
  templateUrl: './information-request-summary.component.html',
  styleUrls: ['./information-request-summary.component.scss']
})
export class InformationRequestSummaryComponent {
  @Input() request: InformationRequest;
}
