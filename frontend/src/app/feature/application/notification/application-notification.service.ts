import {Injectable} from '@angular/core';
import {map} from 'rxjs/internal/operators';
import {ApplicationNotificationType} from '@feature/application/notification/application-notification.component';
import {combineLatest, Observable} from 'rxjs/index';
import * as fromApplication from '@feature/application/reducers';
import * as fromInformationRequest from '@feature/information-request/reducers';
import {InformationRequestStatus} from '@model/information-request/information-request-status';
import {Store} from '@ngrx/store';
import {InformationRequest} from '@model/information-request/information-request';

@Injectable()
export class ApplicationNotificationService {

  constructor(private store: Store<fromApplication.State>) {}

  public getNotificationType(): Observable<ApplicationNotificationType> {
    return combineLatest(
      this.pendingClientData(),
      this.informationRequest(),
      this.informationRequestResponse()
    ).pipe(
      map((types: ApplicationNotificationType[]) => this.pickType(types))
    );
  }

  private pendingClientData(): Observable<ApplicationNotificationType> {
    return this.store.select(fromApplication.hasPendingClientData).pipe(
      map(pending => pending ? ApplicationNotificationType.PENDING_CLIENT_DATA : undefined)
    );
  }

  private informationRequest(): Observable<ApplicationNotificationType> {
    return this.store.select(fromInformationRequest.getInformationRequest).pipe(
      map(request => request ? this.activeInformationRequestNotificationType(request) : undefined)
    );
  }

  private informationRequestResponse(): Observable<ApplicationNotificationType> {
    return this.store.select(fromInformationRequest.getInformationRequestResponse).pipe(
      map(response => response !== undefined ? ApplicationNotificationType.INFORMATION_REQUEST_RESPONSE : undefined)
    );
  }

  private pickType(types: ApplicationNotificationType[] = []): ApplicationNotificationType {
    return types.reduce((acc, cur) => cur !== undefined ? cur : acc, undefined);
  }

  private activeInformationRequestNotificationType(request: InformationRequest): ApplicationNotificationType {
    if (request.status === InformationRequestStatus.OPEN) {
      return ApplicationNotificationType.INFORMATION_REQUEST_PENDING;
    } else if (request.status === InformationRequestStatus.DRAFT) {
      return ApplicationNotificationType.INFORMATION_REQUEST_DRAFT;
    } else {
      return undefined;
    }
  }
}
