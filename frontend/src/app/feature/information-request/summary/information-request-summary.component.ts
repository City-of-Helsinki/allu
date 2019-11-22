import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {InformationRequestSummary} from '@model/information-request/information-request-summary';
import {isRequestActionable, InformationRequestStatus} from '@model/information-request/information-request-status';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {CancelRequest} from '@feature/information-request/actions/information-request-actions';

@Component({
  selector: 'information-request-summary',
  templateUrl: './information-request-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InformationRequestSummaryComponent {
  @Input() summary: InformationRequestSummary;

  constructor(private store: Store<fromRoot.State>) {}

  get showInformationRequestActions() {
    return this.summary ? isRequestActionable(this.summary.status) : false;
  }

  cancelInformationRequest(): void {
    this.store.dispatch(new CancelRequest(this.summary.informationRequestId));
  }
}
