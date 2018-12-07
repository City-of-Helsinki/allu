import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ErrorHandler} from '../error/error-handler.service';
import {Observable} from 'rxjs/index';
import {InformationRequestResponse} from '../../model/information-request/information-request-response';
import {InformationRequest} from '@model/information-request/information-request';
import {BackendInformationRequestResponse, InformationRequestResponseMapper} from '../mapper/information-request-response-mapper';
import {catchError, map} from 'rxjs/internal/operators';
import {findTranslation} from '@util/translations';
import {BackendInformationRequest, InformationRequestMapper} from '../mapper/information-request-mapper';
import {NumberUtil} from '@util/number.util';

const applicationUrl = '/api/applications';
const responseUrlPart = 'informationrequests/response';
const informationRequestUrl = '/api/informationrequests';

@Injectable()
export class InformationRequestService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {}

  getForApplication(id: number): Observable<InformationRequestResponse> {
    const url = `${applicationUrl}/${id}/${responseUrlPart}`;
    return this.http.get<BackendInformationRequestResponse>(url).pipe(
      map(response => InformationRequestResponseMapper.mapBackend(response)),
      catchError(error => this.errorHandler.handle(error, findTranslation('informationRequestResponse.error.fetch')))
    );
  }

  closeInformationRequest(id: number): Observable<InformationRequest> {
    const url = `${informationRequestUrl}/${id}/close`;
    return this.http.put<BackendInformationRequest>(url, {}).pipe(
      map(response => InformationRequestMapper.mapBackend(response)),
      catchError(error => this.errorHandler.handle(error, findTranslation('informationRequestResponse.error.close')))
    );
  }

  getRequestForApplication(id: number): Observable<InformationRequest> {
    const url = `${applicationUrl}/${id}/informationrequests`;
    return this.http.get<BackendInformationRequest>(url).pipe(
      map(response => InformationRequestMapper.mapBackend(response)),
      catchError(error => this.errorHandler.handle(error, findTranslation('informationRequest.error.get')))
    );
  }

  save(request: InformationRequest): Observable<InformationRequest> {
    if (NumberUtil.isDefined(request.informationRequestId)) {
      return this.update(request);
    } else {
      return this.create(request);
    }
  }

  create(request: InformationRequest): Observable<InformationRequest> {
    const url = `${applicationUrl}/${request.applicationId}/informationrequests`;
    return this.http.post<BackendInformationRequest>(url, request).pipe(
      map(response => InformationRequestMapper.mapBackend(response)),
      catchError(error => this.errorHandler.handle(error, findTranslation('informationRequest.error.create')))
    );
  }

  update(request: InformationRequest): Observable<InformationRequest> {
    const url = `${informationRequestUrl}/${request.informationRequestId}`;
    return this.http.put<BackendInformationRequest>(url, InformationRequestMapper.mapFrontend(request)).pipe(
      map(response => InformationRequestMapper.mapBackend(response)),
      catchError(error => this.errorHandler.handle(error, findTranslation('informationRequest.error.update')))
    );
  }

  delete(id: number): Observable<{}> {
    const url = `${informationRequestUrl}/${id}`;
    return this.http.delete<{}>(url).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('informationRequest.error.delete')))
    );
  }
}
