import {Component, OnDestroy, OnInit} from '@angular/core';
import {filter, map, switchMap, take, withLatestFrom} from 'rxjs/operators';
import {EMPTY, Observable, of, Subject} from 'rxjs';
import {MatDialog, MatDialogConfig} from '@angular/material';
import {InformationRequestResult} from '@feature/information-request/information-request-result';
import {SetKindsWithSpecifiers} from '@feature/application/actions/application-actions';
import * as InformationRequestResultAction from '@feature/information-request/actions/information-request-result-actions';
import {
  INFORMATION_ACCEPTANCE_MODAL_CONFIG,
  InformationAcceptanceData,
  InformationAcceptanceModalComponent
} from '@feature/information-request/acceptance/information-acceptance-modal.component';
import {select, Store} from '@ngrx/store';
import * as fromInformationRequest from '@feature/information-request/reducers';
import * as fromApplication from '@feature/application/reducers';
import {Application} from '@model/application/application';
import {
  INFORMATION_REQUEST_MODAL_CONFIG,
  InformationRequestData,
  InformationRequestModalComponent
} from '@feature/information-request/request/information-request-modal.component';
import {InformationRequest} from '@model/information-request/information-request';
import {InformationRequestStatus} from '@model/information-request/information-request-status';
import {SaveAndSendRequest, SaveRequest} from '@feature/information-request/actions/information-request-actions';
import * as fromAuth from '@feature/auth/reducers';
import {RoleType} from '@model/user/role-type';
import {ClientApplicationData} from '@model/application/client-application-data';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import * as fromRoot from '@feature/allu/reducers';
import {ActivatedRoute, Router} from '@angular/router';
import {UrlUtil} from '@util/url.util';

@Component({
  selector: 'information-acceptance-entry',
  templateUrl: './information-acceptance-entry.component.html'
})
export class InformationAcceptanceEntryComponent implements OnInit, OnDestroy {

  private destroy = new Subject<boolean>();

  constructor(
    private dialog: MatDialog,
    private store: Store<fromRoot.State>,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (UrlUtil.urlPathContains(this.route, 'pending_info')) {
      this.showPendingInfo();
    } else if (UrlUtil.urlPathContains(this.route, 'information_request')) {
      this.showInformationRequest();
    }
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  private showPendingInfo(): void {
    this.getPendingData()
      .pipe(
        filter(data => !!data),
        switchMap(data => this.openAcceptanceModal(data))
      ).subscribe((result: InformationRequestResult) => {
        if (result) {
          this.store.dispatch(new SetKindsWithSpecifiers(result.application.kindsWithSpecifiers));
          this.store.dispatch(new InformationRequestResultAction.Save(result));
        }
        this.router.navigate(['../'], {relativeTo: this.route});
      });
  }

  private getPendingData(): Observable<InformationAcceptanceData> {
    return this.store.pipe(
      select(fromInformationRequest.getInformationRequestResponsePending),
      withLatestFrom(this.store.pipe(select(fromApplication.getCurrentApplication))),
      switchMap(([pendingResponse, app]) => {
        if (pendingResponse) {
          return this.getPendingResponse(app);
        } else {
          return this.getPendingInitialInfo(app);
        }
      }),
      take(1)
    );
  }

  private getPendingResponse(currentApp: Application): Observable<InformationAcceptanceData> {
    return this.store.pipe(
      select(fromInformationRequest.getInformationRequestResponse),
      withLatestFrom(this.store.pipe(select(fromInformationRequest.getInformationRequest))),
      filter(([response, request]) => response !== undefined),
      map(([response, request]) => ({
        informationRequest: request,
        oldInfo: currentApp,
        newInfo: response.responseData,
        updatedFields: response.updatedFiedls
      }))
    );
  }

  private getPendingInitialInfo(currentApp: Application): Observable<InformationAcceptanceData> {
    return this.store.pipe(
      select(fromApplication.getClientData),
      filter(clientData => !!clientData),
      map(clientData => this.getPendingDataFields(clientData)),
      switchMap((pending) => {
        if (pending.length) {
          return of({
            oldInfo: currentApp,
            newInfo: currentApp,
            updatedFields: pending
          });
        } else {
          return EMPTY;
        }
      })
    );
  }

  private openAcceptanceModal(data: InformationAcceptanceData): Observable<InformationRequestResult>  {
    return this.createAcceptanceModalConfig(data).pipe(
      switchMap(config => this.dialog.open<InformationAcceptanceModalComponent>(InformationAcceptanceModalComponent, config)
        .afterClosed())
    );
  }

  private showInformationRequest(): void {
    this.store.pipe(
      select(fromInformationRequest.getInformationRequest),
      take(1),
      withLatestFrom(this.store.pipe(select(fromApplication.getCurrentApplication))),
      map(([request, app]) => this.createRequestModalConfig(request, app.id)),
      switchMap(data => this.dialog.open(InformationRequestModalComponent, data).afterClosed()),
    ).subscribe((request: InformationRequest) => {
      this.handleRequest(request);
      this.router.navigate(['../'], {relativeTo: this.route});
    });
  }

  private createAcceptanceModalConfig(baseData: InformationAcceptanceData): Observable<MatDialogConfig<InformationAcceptanceData>> {
    return this.store.pipe(
      select(fromAuth.getUser),
      filter(user => !!user),
      map(user => user.hasRole(RoleType.ROLE_PROCESS_APPLICATION)),
      map(canProcess => {
        const readonly = !canProcess;
        const data = { ...baseData, readonly };
        return {...INFORMATION_ACCEPTANCE_MODAL_CONFIG, data};
      })
    );
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

  private getPendingDataFields(clientData: ClientApplicationData): InformationRequestFieldKey[] {
    let fields: InformationRequestFieldKey[] = [];
    fields = clientData.clientApplicationKind  ? fields.concat(InformationRequestFieldKey.CLIENT_APPLICATION_KIND) : fields;
    fields = clientData.customer ? fields.concat(InformationRequestFieldKey.CUSTOMER) : fields;
    fields = clientData.invoicingCustomer ? fields.concat(InformationRequestFieldKey.INVOICING_CUSTOMER) : fields;
    fields = clientData.representative ? fields.concat(InformationRequestFieldKey.REPRESENTATIVE) : fields;
    fields = clientData.propertyDeveloper ? fields.concat(InformationRequestFieldKey.PROPERTY_DEVELOPER) : fields;
    fields = clientData.contractor ? fields.concat(InformationRequestFieldKey.CONTRACTOR) : fields;
    return fields;
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
