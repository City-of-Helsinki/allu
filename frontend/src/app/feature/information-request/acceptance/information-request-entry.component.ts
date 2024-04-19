import {Component, OnDestroy, OnInit} from '@angular/core';
import {map, switchMap, take, withLatestFrom} from 'rxjs/operators';
import {Subject} from 'rxjs';
import { MatLegacyDialog as MatDialog, MatLegacyDialogConfig as MatDialogConfig } from '@angular/material/legacy-dialog';
import {select, Store} from '@ngrx/store';
import * as fromInformationRequest from '@feature/information-request/reducers';
import * as fromApplication from '@feature/application/reducers';
import {
  INFORMATION_REQUEST_MODAL_CONFIG,
  InformationRequestData,
  InformationRequestModalComponent
} from '@feature/information-request/request/information-request-modal.component';
import {InformationRequest} from '@model/information-request/information-request';
import {InformationRequestStatus} from '@model/information-request/information-request-status';
import {SaveAndSendRequest, SaveRequest} from '@feature/information-request/actions/information-request-actions';
import * as fromRoot from '@feature/allu/reducers';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'information-request-entry',
  template: '<router-outlet></router-outlet>'
})
export class InformationRequestEntryComponent implements OnInit, OnDestroy {

  private destroy = new Subject<boolean>();

  constructor(
    private dialog: MatDialog,
    private store: Store<fromRoot.State>,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.showInformationRequest();
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  private showInformationRequest(): void {
    this.store.pipe(
      select(fromInformationRequest.getActiveInformationRequest),
      take(1),
      withLatestFrom(this.store.pipe(select(fromApplication.getCurrentApplication))),
      map(([request, app]) => this.createRequestModalConfig(request, app.id)),
      switchMap(data => this.dialog.open(InformationRequestModalComponent, data).afterClosed()),
    ).subscribe((request: InformationRequest) => {
      this.handleRequest(request);
      this.router.navigate(['../'], {relativeTo: this.route});
    });
  }

  private createRequestModalConfig(request: InformationRequest, applicationId: number): MatDialogConfig<InformationRequestData> {
    const requestData = request === undefined || request.status === InformationRequestStatus.CLOSED
      ? new InformationRequest(undefined, applicationId, [], InformationRequestStatus.DRAFT)
      : request;

    const data = {request: requestData};

    return {
      ...INFORMATION_REQUEST_MODAL_CONFIG,
      data
    };
  }

  private handleRequest(request: InformationRequest): void {
    if (request) {
      if (InformationRequestStatus.DRAFT === request.status) {
        this.store.dispatch(new SaveRequest(request));
      } else {
        this.store.dispatch(new SaveAndSendRequest(request));
      }
    }
  }
}
