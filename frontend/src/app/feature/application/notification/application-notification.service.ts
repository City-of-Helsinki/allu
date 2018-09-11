import {Injectable} from '@angular/core';
import {ApplicationUtil} from '@feature/application/application-util';
import {map, withLatestFrom} from 'rxjs/internal/operators';
import {ApplicationNotificationType} from '@feature/application/notification/application-notification.component';
import {combineLatest, Observable} from 'rxjs/index';
import * as fromApplication from '@feature/application/reducers';
import * as fromInformationRequest from '@feature/information-request/reducers';
import {InformationRequestStatus} from '@model/information-request/information-request-status';
import {Store} from '@ngrx/store';
import {InformationRequest} from '@model/information-request/information-request';
import {Application} from '@model/application/application';

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
    return combineLatest(
      this.store.select(fromInformationRequest.getInformationRequest),
      this.store.select(fromApplication.getCurrentApplication)
    ).pipe(
      map(([request, app]) => this.openInformationRequest(request, app)),
      map(openRequest => openRequest ? ApplicationNotificationType.INFORMATION_REQUEST_DRAFT : undefined)
    );
  }

  private openInformationRequest(request: InformationRequest, app: Application): boolean {
    const openRequest = request !== undefined ? request.status === InformationRequestStatus.OPEN : false;
    const validForRequest = ApplicationUtil.validForInformationRequest(app);
    return openRequest && validForRequest;
  }

  private informationRequestResponse(): Observable<ApplicationNotificationType> {
    return this.store.select(fromInformationRequest.getInformationRequestResponse).pipe(
      map(response => response !== undefined ? ApplicationNotificationType.INFORMATION_REQUEST_RESPONSE : undefined)
    );
  }

  private pickType(types: ApplicationNotificationType[] = []): ApplicationNotificationType {
    return types.reduce((acc, cur) => cur !== undefined ? cur : acc, undefined);
  }
}
