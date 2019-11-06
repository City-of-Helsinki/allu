import {Component, Input, OnInit} from '@angular/core';
import {InformationRequestSummary} from '@model/information-request/information-request-summary';
import {select, Store} from '@ngrx/store';
import * as fromInformationRequest from '@feature/information-request/reducers';
import {Observable} from 'rxjs';
import {Get} from '@feature/information-request/actions/information-request-summary-actions';

@Component({
  selector: 'information-request-summaries',
  templateUrl: './information-request-summaries.component.html'
})
export class InformationRequestSummariesComponent implements OnInit {
  loading$: Observable<boolean>;
  summaries$: Observable<InformationRequestSummary[]>;

  constructor(private store: Store<fromInformationRequest.State>) {
  }

  ngOnInit(): void {
    this.summaries$ = this.store.pipe(select(fromInformationRequest.getSummaries));
    this.loading$ = this.store.pipe(select(fromInformationRequest.getSummariesLoading));

    this.store.dispatch(new Get());
  }
}
