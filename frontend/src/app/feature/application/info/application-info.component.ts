import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material';
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
import {filter, map} from 'rxjs/internal/operators';

@Component({
  selector: 'application-info',
  viewProviders: [],
  templateUrl: './application-info.component.html',
  styleUrls: []
})
export class ApplicationInfoComponent implements OnInit, CanComponentDeactivate {

  type: string;
  showDraftSelection: boolean;
  readonly: boolean;
  formDirty: boolean;
  notificationType$: Observable<ApplicationNotificationType>;

  constructor(private applicationStore: ApplicationStore,
              private route: ActivatedRoute,
              private dialog: MatDialog,
              private store: Store<fromApplication.State>) {}

  ngOnInit(): void {
    const application = this.applicationStore.snapshot.application;
    this.type = application.type;
    this.showDraftSelection = this.shouldShowDraftSelection();

    this.readonly = UrlUtil.urlPathContains(this.route.parent, 'summary');
    this.formDirty = false;
    this.notificationType$ = this.getNotificationType();
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

  private getNotificationType(): Observable<ApplicationNotificationType> {
    return merge(
      this.store.select(fromApplication.hasPendingClientData).pipe(
        map(pending => pending ? ApplicationNotificationType.PENDING_CLIENT_DATA : undefined)
      ),
      this.store.select(fromInformationRequest.getInformationRequest).pipe(
        map(request => !!request ? ApplicationNotificationType.INFORMATION_REQUEST_DRAFT : undefined)
      ),
      this.store.select(fromInformationRequest.getInformationRequestResponse).pipe(
        map(response => !!response ? ApplicationNotificationType.INFORMATION_REQUEST_RESPONSE : undefined)
      )
    ).pipe(
      filter(type => type !== undefined)
    );
  }
}
