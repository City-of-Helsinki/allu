import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatDialog, MatDialogConfig} from '@angular/material';
import {merge, Observable} from 'rxjs';
import {ApplicationStore} from '../../../service/application/application-store';
import {UrlUtil} from '../../../util/url.util';
import {ActivatedRoute} from '@angular/router';
import {CanComponentDeactivate} from '../../../service/common/can-deactivate-guard';
import {findTranslation} from '../../../util/translations';
import {ConfirmDialogComponent} from '../../common/confirm-dialog/confirm-dialog.component';
import {ApplicationNotificationType} from '@feature/application/notification/application-notification.component';
import {Store} from '@ngrx/store';
import * as fromApplication from '../reducers';
import * as fromInformationRequest from '@feature/information-request/reducers';
import {filter, map, scan, switchMap, take, takeUntil, withLatestFrom} from 'rxjs/internal/operators';
import * as InformationRequestResultAction from '@feature/information-request/actions/information-request-result-actions';
import {InformationRequestResult} from '@feature/information-request/information-request-result';
import {SetKindsWithSpecifiers} from '@feature/application/actions/application-actions';
import {EMPTY, of, Subject} from 'rxjs/index';
import {
  INFORMATION_ACCEPTANCE_MODAL_CONFIG,
  InformationAcceptanceData,
  InformationAcceptanceModalComponent
} from '@feature/information-request/acceptance/information-acceptance-modal.component';
import {ApplicationStatus} from '@model/application/application-status';
import {Application} from '@model/application/application';
import {InformationRequestModalEvents} from '@feature/information-request/information-request-modal-events';
import {InformationRequest} from '@model/information-request/information-request';
import {
  InformationRequestInfo,
  InformationRequestModalComponent
} from '@feature/information-request/request/information-request-modal.component';
import {InformationRequestStatus} from '@model/information-request/information-request-status';
import {SaveAndSendRequest, SaveRequest} from '@feature/information-request/actions/information-request-actions';
import {ApplicationUtil} from '@feature/application/application-util';
import {ApplicationNotificationService} from '@feature/application/notification/application-notification.service';

@Component({
  selector: 'application-info',
  viewProviders: [],
  templateUrl: './application-info.component.html',
  styleUrls: []
})
export class ApplicationInfoComponent implements OnInit, CanComponentDeactivate, OnDestroy {

  type: string;
  showDraftSelection: boolean;
  readonly: boolean;
  formDirty: boolean;
  notificationType$: Observable<ApplicationNotificationType>;

  private destroy = new Subject<boolean>();

  constructor(private applicationStore: ApplicationStore,
              private route: ActivatedRoute,
              private store: Store<fromApplication.State>,
              private dialog: MatDialog,
              private modalState: InformationRequestModalEvents,
              private applicationNotificationService: ApplicationNotificationService) {}

  ngOnInit(): void {
    const application = this.applicationStore.snapshot.application;
    this.type = application.type;
    this.showDraftSelection = this.shouldShowDraftSelection();

    this.readonly = UrlUtil.urlPathContains(this.route.parent, 'summary');
    this.formDirty = false;
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

  formDirtyChanged(dirty: boolean): void {
    this.formDirty = dirty;
  }

  canDeactivate(): Observable<boolean> | boolean {
    if (this.formDirty) {
      return this.confirmChanges();
    } else {
      return true;
    }
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
        if (ApplicationStatus.INFORMATION_RECEIVED === app.statusEnum) {
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
    data.readonly = this.applicationStore.snapshot.application.status === ApplicationStatus[ApplicationStatus.PENDING_CLIENT];
    const config: MatDialogConfig<InformationAcceptanceData> = {...INFORMATION_ACCEPTANCE_MODAL_CONFIG, data};
    return this.dialog
      .open<InformationAcceptanceModalComponent>(InformationAcceptanceModalComponent, config)
      .afterClosed()
      .pipe(filter(result => !!result));
  }

  private showInformationRequest(): void {
    this.store.select(fromInformationRequest.getInformationRequest).pipe(
      take(1),
      withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
      map(([request, app]) => request !== undefined
        ? request
        : new InformationRequest(undefined, app.id, [], InformationRequestStatus.OPEN)),
      map(request => ({data: { request }})),
      switchMap(data => this.dialog.open(InformationRequestModalComponent, data).afterClosed()),
      filter(result => !!result) // Ignore no answers
    ).subscribe((result: InformationRequestInfo) => {
      if (result.draft) {
        this.store.dispatch(new SaveRequest(result.request));
      } else {
        this.store.dispatch(new SaveAndSendRequest(result.request));
      }
    });
  }

}
