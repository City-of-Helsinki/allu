import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatDialog, MatDialogConfig} from '@angular/material';
import {Observable} from 'rxjs';
import {ApplicationStore} from '@service/application/application-store';
import {UrlUtil} from '@util/url.util';
import {ActivatedRoute} from '@angular/router';
import {CanComponentDeactivate} from '@service/common/can-deactivate-guard';
import {findTranslation} from '../../../util/translations';
import {ConfirmDialogComponent} from '../../common/confirm-dialog/confirm-dialog.component';
import {ApplicationNotificationType} from '@feature/application/notification/application-notification.component';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromAuth from '@feature/auth/reducers';
import * as fromApplication from '../reducers';
import * as fromInformationRequest from '@feature/information-request/reducers';
import {filter, map, switchMap, take, takeUntil, withLatestFrom} from 'rxjs/internal/operators';
import * as InformationRequestResultAction from '@feature/information-request/actions/information-request-result-actions';
import {InformationRequestResult} from '@feature/information-request/information-request-result';
import {SetKindsWithSpecifiers} from '@feature/application/actions/application-actions';
import {EMPTY, of, Subject} from 'rxjs/index';
import {
  INFORMATION_ACCEPTANCE_MODAL_CONFIG,
  InformationAcceptanceData,
  InformationAcceptanceModalComponent
} from '@feature/information-request/acceptance/information-acceptance-modal.component';
import {ApplicationStatus, isBefore} from '@model/application/application-status';
import {Application} from '@model/application/application';
import {InformationRequestModalEvents} from '@feature/information-request/information-request-modal-events';
import {InformationRequest} from '@model/information-request/information-request';
import {
  InformationRequestData,
  InformationRequestModalComponent,
  INFORMATION_REQUEST_MODAL_CONFIG
} from '@feature/information-request/request/information-request-modal.component';
import {InformationRequestStatus} from '@model/information-request/information-request-status';
import {SaveAndSendRequest, SaveRequest} from '@feature/information-request/actions/information-request-actions';
import {ApplicationNotificationService} from '@feature/application/notification/application-notification.service';
import {FormBuilder, FormGroup} from '@angular/forms';
import {applicationForm} from '@feature/application/info/application-form';
import {RoleType} from '@model/user/role-type';

@Component({
  selector: 'application-info',
  viewProviders: [],
  templateUrl: './application-info.component.html',
  styleUrls: []
})
export class ApplicationInfoComponent implements OnInit, CanComponentDeactivate, OnDestroy {

  form: FormGroup;
  type: string;
  showDraftSelection: boolean;
  readonly: boolean;
  notificationType$: Observable<ApplicationNotificationType>;

  private destroy = new Subject<boolean>();

  constructor(private applicationStore: ApplicationStore,
              private route: ActivatedRoute,
              private store: Store<fromRoot.State>,
              private dialog: MatDialog,
              private modalState: InformationRequestModalEvents,
              private applicationNotificationService: ApplicationNotificationService,
              private fb: FormBuilder) {}

  ngOnInit(): void {
    const application = this.applicationStore.snapshot.application;
    this.form = this.fb.group(applicationForm(application));
    this.type = application.type;
    this.showDraftSelection = this.shouldShowDraftSelection();

    this.readonly = UrlUtil.urlPathContains(this.route.parent, 'summary');
    this.notificationType$ = this.applicationNotificationService.getNotificationType();

    this.modalState.isAcceptanceOpen$.pipe(
      takeUntil(this.destroy),
      filter(open => open)
    ).subscribe(() => this.showPendingInfo());

    this.modalState.isRequestOpen$.pipe(
      takeUntil(this.destroy),
      filter(open => open)
    ).subscribe(() => this.showInformationRequest());
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  canDeactivate(): Observable<boolean> | boolean {
    if (this.form.dirty && this.form.touched) {
      return this.confirmChanges();
    } else {
      return true;
    }
  }

  updateReceivedTime(date: Date): void {
    this.form.patchValue({receivedTime: date});
  }

  private confirmChanges(): Observable<boolean> {
    const data = {
      title: findTranslation(['application.confirmDiscard.title']),
      description: findTranslation(['application.confirmDiscard.description']),
      confirmText: findTranslation(['application.confirmDiscard.confirmText']),
      cancelText: findTranslation(['application.confirmDiscard.cancelText'])
    };
    return this.dialog.open(ConfirmDialogComponent, {data}).afterClosed();
  }

  private shouldShowDraftSelection() {
    return this.applicationStore.isNew &&
        this.applicationStore.snapshot.application.type !== 'TEMPORARY_TRAFFIC_ARRANGEMENTS';
  }

  private showPendingInfo(): void {
    this.getPendingData()
      .pipe(switchMap(data => this.openAcceptanceModal(data)))
      .subscribe((result: InformationRequestResult) => {
        this.store.dispatch(new SetKindsWithSpecifiers(result.application.kindsWithSpecifiers));
        this.store.dispatch(new InformationRequestResultAction.Save(result));
      });
  }

  private getPendingData(): Observable<InformationAcceptanceData> {
    return this.store.select(fromApplication.getCurrentApplication).pipe(
      switchMap(app => {
        if (ApplicationStatus.INFORMATION_RECEIVED === app.status) {
          return this.getPendingResponse(app);
        } else {
          return this.getPendingInitialInfo(app);
        }
      }),
      take(1)
    );
  }

  private getPendingResponse(currentApp: Application): Observable<InformationAcceptanceData> {
    return this.store.select(fromInformationRequest.getInformationRequestResponse).pipe(
      filter(response => response !== undefined),
      map(response => ({
        informationRequestId: response.informationRequestId,
        oldInfo: currentApp,
        newInfo: response.responseData,
        updatedFields: response.updatedFiedls
      }))
    );
  }

  private getPendingInitialInfo(currentApp: Application): Observable<InformationAcceptanceData> {
    return this.store.select(fromApplication.pendingClientDataFields).pipe(
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
        .afterClosed()),
      filter(result => !!result)
    );
  }

  private showInformationRequest(): void {
    this.store.select(fromInformationRequest.getInformationRequest).pipe(
      take(1),
      withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
      map(([request, app]) => this.createRequestModalConfig(request, app.id)),
      switchMap(data => this.dialog.open(InformationRequestModalComponent, data).afterClosed()),
      filter(request => !!request), // Ignore no answers
    ).subscribe((request: InformationRequest) => {
      if (InformationRequestStatus.DRAFT === request.status) {
        this.store.dispatch(new SaveRequest(request));
      } else {
        this.store.dispatch(new SaveAndSendRequest(request));
      }
    });
  }

  private createAcceptanceModalConfig(baseData: InformationAcceptanceData): Observable<MatDialogConfig<InformationAcceptanceData>> {
    const readonlyStatus = isBefore(this.applicationStore.snapshot.application.status, ApplicationStatus.INFORMATION_RECEIVED);

    return this.store.pipe(
      select(fromAuth.getUser),
      filter(user => !!user),
      map(user => user.hasRole(RoleType.ROLE_PROCESS_APPLICATION)),
      map(canProcess => {
        const readonly = readonlyStatus || !canProcess;
        const data = { ...baseData, readonly };
        return {...INFORMATION_ACCEPTANCE_MODAL_CONFIG, data};
      })
    );
  }

  private createRequestModalConfig(request: InformationRequest, applicationId: number): MatDialogConfig<InformationRequestData> {
    const result = request !== undefined
      ? request
      : new InformationRequest(undefined, applicationId, [], InformationRequestStatus.DRAFT);

    const data = {request: result};

    return {
      ...INFORMATION_REQUEST_MODAL_CONFIG,
      data
    };
  }
}
