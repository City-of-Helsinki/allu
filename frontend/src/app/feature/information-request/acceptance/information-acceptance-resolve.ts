import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {InformationAcceptanceData} from '@feature/information-request/acceptance/information-acceptance-modal.component';
import {EMPTY, forkJoin, Observable, of} from 'rxjs';
import {Injectable} from '@angular/core';
import {select, Store} from '@ngrx/store';
import * as fromInformationRequest from '@feature/information-request/reducers';
import {filter, map, switchMap, take, withLatestFrom} from 'rxjs/operators';
import * as fromApplication from '@feature/application/reducers';
import {Application} from '@model/application/application';
import * as fromRoot from '@feature/allu/reducers';
import {ClientApplicationData} from '@model/application/client-application-data';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';

@Injectable()
export class InformationAcceptanceResolve implements Resolve<InformationAcceptanceData> {
  constructor(private store: Store<fromRoot.State>) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<InformationAcceptanceData> {
    return this.getPendingData();
  }

  private getPendingData(): Observable<InformationAcceptanceData> {
    return this.store.pipe(
      select(fromInformationRequest.getActiveInformationRequestResponsePending),
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
      select(fromInformationRequest.getActiveInformationRequest),
      switchMap(request => forkJoin([
        of(request),
        this.store.pipe(select(fromInformationRequest.getInformationRequestResponse(request.informationRequestId)), take(1))
      ])),
      filter(([request, response]) => response !== undefined),
      map(([request, response]) => ({
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
