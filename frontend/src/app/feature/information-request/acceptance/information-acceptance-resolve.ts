import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import {InformationAcceptanceData} from '@feature/information-request/acceptance/information-acceptance-modal.component';
import {combineLatest, EMPTY, forkJoin, Observable, of} from 'rxjs';
import {Injectable} from '@angular/core';
import {select, Store} from '@ngrx/store';
import * as fromInformationRequest from '@feature/information-request/reducers';
import {catchError, filter, map, switchMap, take} from 'rxjs/operators';
import * as fromApplication from '@feature/application/reducers';
import * as fromRoot from '@feature/allu/reducers';
import {ClientApplicationData} from '@model/application/client-application-data';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {NumberUtil} from '@util/number.util';
import {GetRequest} from '@feature/information-request/actions/information-request-actions';
import {GetResponse} from '@feature/information-request/actions/information-request-response-actions';
import {Application} from '@model/application/application';
import {InformationRequestResponse} from '@model/information-request/information-request-response';
import {ObjectUtil} from '@util/object.util';
import {ApplicationStatus} from '@model/application/application-status';
import {InformationRequestService} from '@service/application/information-request.service';
import {canHaveResponse} from '@model/information-request/information-request-status';

@Injectable()
export class InformationAcceptanceResolve  {
  constructor(
    private store: Store<fromRoot.State>,
    private informationRequestService: InformationRequestService
  ) {}

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
      take(1),
      switchMap(currentApp => {
        // When the application is in INFORMATION_RECEIVED status, there should be
        // an info request response that may contain customer removals (null values).
        // Fetch the response directly from the API to avoid NgRx store timing issues.
        if (currentApp.status === ApplicationStatus.INFORMATION_RECEIVED) {
          return this.informationRequestService.getRequestForApplication(currentApp.id).pipe(
            switchMap(request => {
              if (request && canHaveResponse(request.status)) {
                return this.informationRequestService.getResponseForRequest(request.informationRequestId).pipe(
                  map(response => this.buildAcceptanceDataWithResponse(currentApp, pending, response)),
                  catchError(() => this.fallbackAcceptanceData(currentApp, pending))
                );
              }
              return this.fallbackAcceptanceData(currentApp, pending);
            }),
            catchError(() => this.fallbackAcceptanceData(currentApp, pending))
          );
        }

        if (pending.length) {
          return of({
            oldInfo: currentApp,
            newInfo: currentApp,
            updatedFields: pending
          });
        }
        return EMPTY;
      })
    );
  }

  private fallbackAcceptanceData(currentApp: Application, pending: InformationRequestFieldKey[]): Observable<InformationAcceptanceData> {
    if (pending.length) {
      return of({
        oldInfo: currentApp,
        newInfo: currentApp,
        updatedFields: pending
      });
    }
    return EMPTY;
  }

  /**
   * Build acceptance data by merging the info request response's clientApplicationData
   * into the current application. This handles the case where the response removes a
   * customer role (e.g. representative set to null).
   */
  private buildAcceptanceDataWithResponse(
    currentApp: Application,
    pendingFields: InformationRequestFieldKey[],
    response: InformationRequestResponse
  ): InformationAcceptanceData {
    if (!response || !response.responseData) {
      return pendingFields.length ? { oldInfo: currentApp, newInfo: currentApp, updatedFields: pendingFields } : undefined;
    }

    const responseClientData = response.responseData.clientApplicationData;
    const responseFields = response.updatedFiedls || [];

    // Build newInfo with the response's clientApplicationData merged over the app's
    const newInfo: Application = ObjectUtil.clone(currentApp);
    if (newInfo.clientApplicationData && responseClientData) {
      for (const field of responseFields) {
        switch (field) {
          case InformationRequestFieldKey.CUSTOMER:
            newInfo.clientApplicationData.customer = responseClientData.customer;
            break;
          case InformationRequestFieldKey.REPRESENTATIVE:
            newInfo.clientApplicationData.representative = responseClientData.representative;
            break;
          case InformationRequestFieldKey.PROPERTY_DEVELOPER:
            newInfo.clientApplicationData.propertyDeveloper = responseClientData.propertyDeveloper;
            break;
          case InformationRequestFieldKey.CONTRACTOR:
            newInfo.clientApplicationData.contractor = responseClientData.contractor;
            break;
          case InformationRequestFieldKey.INVOICING_CUSTOMER:
            newInfo.clientApplicationData.invoicingCustomer = responseClientData.invoicingCustomer;
            break;
          case InformationRequestFieldKey.CLIENT_APPLICATION_KIND:
            newInfo.clientApplicationData.clientApplicationKind = responseClientData.clientApplicationKind;
            break;
        }
      }
    } else if (responseClientData) {
      newInfo.clientApplicationData = responseClientData;
    }

    // Merge updated fields: combine pending clientData fields with response fields
    const allFields = [...pendingFields];
    for (const field of responseFields) {
      if (!allFields.includes(field)) {
        allFields.push(field);
      }
    }

    // Re-evaluate pending fields after merge: include fields from the response
    // that indicate removal (null value), which getPendingDataFields would have missed
    const mergedFields = this.getPendingDataFields(newInfo.clientApplicationData || new ClientApplicationData());
    for (const field of responseFields) {
      if (!mergedFields.includes(field)) {
        mergedFields.push(field);
      }
    }

    if (mergedFields.length === 0) {
      return undefined;
    }

    return {
      oldInfo: currentApp,
      newInfo: newInfo,
      updatedFields: mergedFields,
      informationRequest: undefined
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
}
