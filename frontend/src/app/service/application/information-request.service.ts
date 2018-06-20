import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ErrorHandler} from '../error/error-handler.service';
import {Observable} from 'rxjs/index';
import {InformationRequestResponse} from '../../model/information-request/information-request-response';
import {BackendInformationRequestResponse, InformationRequestResponseMapper} from '../mapper/information-request-response-mapper';
import {catchError, map} from 'rxjs/internal/operators';

const applicationUrl = '/api/applications';
const responseUrlPart = 'informationrequests/response';

@Injectable()
export class InformationRequestService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {}

  getForApplication(id: number): Observable<InformationRequestResponse> {
    const url = `${applicationUrl}/${id}/${responseUrlPart}`;
    return this.http.get<BackendInformationRequestResponse>(url).pipe(
      map(response => InformationRequestResponseMapper.mapBackend(response)),
      catchError(error => this.errorHandler.handle(error, 'informationRequestResponse.error.fetch'))
    );
  }
}
