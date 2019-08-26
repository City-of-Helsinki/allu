import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ErrorHandler} from '@service/error/error-handler.service';
import {Observable} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {TerminationInfo} from '@feature/decision/termination/termination-info';
import {BackendTerminationInfo, TerminationInfoMapper} from '@feature/decision/termination/termination-mapper';
import {findTranslation} from '@util/translations';
import {NumberUtil} from '@util/number.util';
import {TerminationDocument} from '@feature/decision/termination/TerminationDocument';
import {ApplicationStore} from '@service/application/application-store';

const APPLICATION_URL = '/api/applications';

@Injectable()
export class TerminationService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler, private applicationStore: ApplicationStore ) {
  }

  getTermination(applicationId: number): Observable<TerminationDocument> {
    const url = `${APPLICATION_URL}/${applicationId}/termination`;
    return this.http.get(url, {responseType: 'blob'}).pipe(
      map(pdf => new TerminationDocument(applicationId, pdf)),
      catchError(error => this.errorHandler.handle(error, findTranslation('termination.error.fetchDocument')))
    );
  }

  getTerminationInfo(applicationId: number): Observable<TerminationInfo> {
    const url = `${APPLICATION_URL}/${applicationId}/termination/info`;
    return this.http.get<BackendTerminationInfo>(url).pipe(
      map(termination => TerminationInfoMapper.mapBackend(termination)),
      catchError(error => this.errorHandler.handle(error, findTranslation('termination.error.fetchInfo')))
    );
  }

  saveTerminationInfo(applicationId: number, terminationInfo: TerminationInfo): Observable<TerminationInfo> {
    return NumberUtil.isExisting(terminationInfo)
      ? this.updateTerminationInfo(applicationId, terminationInfo)
      : this.insertTerminationInfo(applicationId, terminationInfo);
  }

  removeTerminationInfo(applicationId: number): Observable<TerminationInfo> {
    const url = `${APPLICATION_URL}/${applicationId}/termination/info`;
    return this.http.delete<BackendTerminationInfo>(url).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('termination.error.removeInfo')))
    );
  }

  private insertTerminationInfo(applicationId: number, terminationInfo: TerminationInfo) {
    const url = `${APPLICATION_URL}/${applicationId}/termination/info`;
    const info = JSON.stringify(TerminationInfoMapper.mapFrontEnd(terminationInfo));
    return this.http.post<BackendTerminationInfo>(url, info).pipe(
      map(saved => TerminationInfoMapper.mapBackend(saved)),
      catchError(error => this.errorHandler.handle(error, findTranslation('termination.error.saveInfo')))
    );
  }

  private updateTerminationInfo(applicationId: number, terminationInfo: TerminationInfo) {
    const url = `${APPLICATION_URL}/${applicationId}/termination/info`;
    const info = JSON.stringify(TerminationInfoMapper.mapFrontEnd(terminationInfo));
    return this.http.put<BackendTerminationInfo>(url, info).pipe(
      map(saved => TerminationInfoMapper.mapBackend(saved)),
      catchError(error => this.errorHandler.handle(error, findTranslation('termination.error.saveInfo')))
    );
  }
}
