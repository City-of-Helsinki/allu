import {Injectable} from '@angular/core';
import {map} from 'rxjs/internal/operators';
import {combineLatest, Observable} from 'rxjs/index';
import * as fromApplication from '@feature/application/reducers';
import * as fromInformationRequest from '@feature/information-request/reducers';
import {InformationRequestStatus} from '@model/information-request/information-request-status';
import {Store} from '@ngrx/store';
import {InformationRequest} from '@model/information-request/information-request';
import {ExternalUpdateNotificationType} from '@feature/application/notification/external-update/external-update-notification.component';

@Injectable()
export class ExternalUpdateNotificationService {

  constructor(private store: Store<fromApplication.State>) {}

  public getNotificationType(): Observable<ExternalUpdateNotificationType> {
    return combineLatest([
      this.pendingClientData(),
      this.informationRequest()
    ]
    ).pipe(
      map((types: ExternalUpdateNotificationType[]) => this.pickType(types))
    );
  }

  private pendingClientData(): Observable<ExternalUpdateNotificationType> {
    return this.store.select(fromApplication.hasPendingClientData).pipe(
      map(pending => pending ? ExternalUpdateNotificationType.PENDING_CLIENT_DATA : undefined)
    );
  }

  private informationRequest(): Observable<ExternalUpdateNotificationType> {
    return this.store.select(fromInformationRequest.getActiveInformationRequest).pipe(
      map(request => request ? this.activeInformationRequestNotificationType(request) : undefined)
    );
  }

  private pickType(types: ExternalUpdateNotificationType[] = []): ExternalUpdateNotificationType {
    return types.reduce((acc, cur) => cur !== undefined ? cur : acc, undefined);
  }

  private activeInformationRequestNotificationType(request: InformationRequest): ExternalUpdateNotificationType {
    switch (request.status) {
      case InformationRequestStatus.OPEN:
        return ExternalUpdateNotificationType.INFORMATION_REQUEST_PENDING;
      case InformationRequestStatus.DRAFT:
        return ExternalUpdateNotificationType.INFORMATION_REQUEST_DRAFT;
      case InformationRequestStatus.RESPONSE_RECEIVED:
        return ExternalUpdateNotificationType.INFORMATION_REQUEST_RESPONSE;
      default:
        return undefined;
    }
  }
}
