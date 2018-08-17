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

  // TODO: Add information request as return type when implemented
  closeInformationRequest(id: number): Observable<any> {
    const url = `${informationRequestUrl}/${id}/close`;
    return this.http.put<any>(url, {}).pipe(
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

  create(request: InformationRequest): Observable<InformationRequest> {
    const url = `${applicationUrl}/${request.applicationId}/informationrequests`;
    return this.http.post<InformationRequest>(url, request).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('informationRequest.error.create')))
    );
  }

  update(request: InformationRequest): Observable<InformationRequest> {
    const url = `${informationRequestUrl}/${request.informationRequestId}`;
    return this.http.put<InformationRequest>(url, InformationRequestMapper.mapFrontend(request)).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('informationRequest.error.update')))
    );
  }
}
