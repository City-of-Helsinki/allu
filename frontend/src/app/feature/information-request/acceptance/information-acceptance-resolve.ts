import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import {InformationAcceptanceData} from '@feature/information-request/acceptance/information-acceptance-modal.component';
import {combineLatest, EMPTY, forkJoin, Observable, of} from 'rxjs';
import {Injectable} from '@angular/core';
import {select, Store} from '@ngrx/store';
import * as fromInformationRequest from '@feature/information-request/reducers';
import {filter, map, switchMap, take} from 'rxjs/operators';
import * as fromApplication from '@feature/application/reducers';
import * as fromRoot from '@feature/allu/reducers';
import {ClientApplicationData} from '@model/application/client-application-data';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {NumberUtil} from '@util/number.util';
import {GetRequest} from '@feature/information-request/actions/information-request-actions';
import {GetResponse} from '@feature/information-request/actions/information-request-response-actions';

@Injectable()
export class InformationAcceptanceResolve  {
  constructor(private store: Store<fromRoot.State>) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<InformationAcceptanceData> {
    return this.store.pipe(
      select(fromApplication.getClientData),
      switchMap(clientData => {
        if (clientData) {
          return this.clientDataToInformationAcceptanceData(clientData);
        } else {
          return this.getRequestId(route.params['id']).pipe(
            switchMap(id => this.loadInformationAcceptanceData(id))
          );
        }
      }),
      take(1)
    );
  }

  /**
   * When request id is provided use it, otherwise check if there are
   * pending request and use it's id
   */
  private getRequestId(requestId?: number): Observable<number> {
    return NumberUtil.isDefined(requestId)
      ? of(requestId)
      : this.getPendingRequestId();
  }

  private getPendingRequestId(): Observable<number> {
    return this.store.pipe(
      select(fromInformationRequest.getActiveInformationRequestResponsePending),
      filter(pending => pending),
      switchMap(() => this.store.pipe(select(fromInformationRequest.getActiveInformationRequestId))),
      take(1)
    );
  }

  private loadInformationAcceptanceData(requestId: number): Observable<InformationAcceptanceData> {
    return this.loadAndWait(requestId).pipe(
      filter(loadingDone => loadingDone),
      switchMap(() => this.gatherAcceptanceData(requestId)),
      map(([request, response, currentApp]) => ({
        informationRequest: request,
        oldInfo: currentApp,
        newInfo: response.responseData,
        updatedFields: response.updatedFiedls
      }))
    );
  }

  private loadAndWait(requestId: number): Observable<boolean> {
    this.store.dispatch(new GetRequest(requestId));
    this.store.dispatch(new GetResponse(requestId));

    return combineLatest([
      this.store.pipe(select(fromInformationRequest.getInformationRequestLoading)),
      this.store.pipe(select(fromInformationRequest.getInformationRequestResponseLoading))
    ]).pipe(
      map(([requestLoading, responseLoading]) => !(requestLoading || responseLoading))
    );
  }

  private gatherAcceptanceData(requestId: number) {
    return this.store.pipe(
      select(fromInformationRequest.getInformationRequest(requestId)),
      switchMap(request => forkJoin([
        of(request),
        this.store.pipe(select(fromInformationRequest.getInformationRequestResponse(requestId)), take(1)),
        this.store.pipe(select(fromApplication.getCurrentApplication), take(1))
      ])),
      filter(([request, response, currentApp]) => response !== undefined),
    );
  }

  private clientDataToInformationAcceptanceData(clientData: ClientApplicationData): Observable<InformationAcceptanceData> {
    const pending = this.getPendingDataFields(clientData);

    return this.store.pipe(
      select(fromApplication.getCurrentApplication),
      switchMap((currentApp) => {
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
}
