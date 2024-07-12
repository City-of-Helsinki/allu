import {ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {MatLegacyDialog as MatDialog} from '@angular/material/legacy-dialog';
import {combineLatest, Observable, Subject} from 'rxjs';
import {ApplicationStore} from '@service/application/application-store';
import {UrlUtil} from '@util/url.util';
import {ActivatedRoute} from '@angular/router';
import {CanComponentDeactivate} from '@service/common/can-deactivate-guard';
import {findTranslation} from '../../../util/translations';
import {ConfirmDialogComponent} from '../../common/confirm-dialog/confirm-dialog.component';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {getLoggedInUser} from '@feature/auth/reducers';
import * as fromApplication from '../reducers';
import {map, take, takeUntil} from 'rxjs/operators';
import {ApplicationStatus} from '@model/application/application-status';
import {Application} from '@model/application/application';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {applicationForm} from '@feature/application/info/application-form';
import {ApplicationType} from '@model/application/type/application-type';
import {ExternalUpdateNotificationType} from '@feature/application/notification/external-update/external-update-notification.component';
import {ExternalUpdateNotificationService} from '@feature/application/notification/external-update/external-update-notification.service';

@Component({
  selector: 'application-info',
  viewProviders: [],
  templateUrl: './application-info.component.html',
  styleUrls: []
})
export class ApplicationInfoComponent implements OnInit, CanComponentDeactivate, OnDestroy {

  form: UntypedFormGroup;
  type: string;
  showDraftSelection: boolean;
  readonly: boolean;
  notificationType$: Observable<ExternalUpdateNotificationType>;
  application$: Observable<Application>;
  showOwnerNotification$: Observable<boolean>;

  private destroy = new Subject<boolean>();

  constructor(private applicationStore: ApplicationStore,
              private route: ActivatedRoute,
              private store: Store<fromRoot.State>,
              private dialog: MatDialog,
              private cdr: ChangeDetectorRef,
              private applicationNotificationService: ExternalUpdateNotificationService,
              private fb: UntypedFormBuilder) {}

  ngOnInit(): void {
    this.initForm();

    this.application$ = this.store.pipe(select(fromApplication.getCurrentApplication));
    this.application$.pipe(takeUntil(this.destroy)).subscribe(() => this.cdr.detectChanges);

    this.readonly = UrlUtil.urlPathContains(this.route.parent, 'summary');
    this.notificationType$ = this.applicationNotificationService.getNotificationType();

    this.showOwnerNotification$ = combineLatest([
      this.store.pipe(select(fromApplication.getCurrentApplication)),
      getLoggedInUser(this.store)
    ]).pipe(
      map(([app, user]) => app.ownerNotification && app.owner && app.owner.id === user.id)
    );
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

  private shouldShowDraftSelection(application: Application) {
    const preReserved = application.status === ApplicationStatus.PRE_RESERVED;
    const targetedForPending = application.targetState === ApplicationStatus.PENDING;
    const typeCanHaveDraft = [ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS, ApplicationType.NOTE].indexOf(application.type) < 0;
    return preReserved && targetedForPending && typeCanHaveDraft;
  }

  private initForm(): void {
    this.store.pipe(
      select(fromApplication.getCurrentApplication),
      take(1)
    ).subscribe(app => {
      this.form = this.fb.group(applicationForm(app));
      this.type = app.type;
      this.showDraftSelection = this.shouldShowDraftSelection(app);
    });
  }
}
