import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {catchError, map} from 'rxjs/internal/operators';
import {ErrorHandler} from '@service/error/error-handler.service';
import {Contract} from '@model/contract/contract';
import {ContractInfo} from '@model/contract/contract-info';
import {findTranslation} from '@util/translations';

const URL_PREFIX = '/api/applications';

@Injectable()
export class ContractService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  public fetchPreview(applicationId: number): Observable<Contract> {
    const url = `${URL_PREFIX}/${applicationId}/contract/preview`;

    return this.http.get(url, {responseType: 'blob'}).pipe(
      map(pdf => new Contract(applicationId, pdf))
    );
  }

  public fetch(applicationId: number): Observable<Contract> {
    const url = `${URL_PREFIX}/${applicationId}/contract`;

    return this.http.get(url, {responseType: 'blob'}).pipe(
      map(pdf => new Contract(applicationId, pdf))
    );
  }

  public createProposal(applicationId: number): Observable<{}> {
    const url = `${URL_PREFIX}/${applicationId}/contract/proposal`;

    return this.http.post(url, null).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('contract.error.createProposalFailed')))
    );
  }

  public approve(applicationId: number, contractInfo: ContractInfo): Observable<{}> {
    const url = `${URL_PREFIX}/${applicationId}/contract/approved`;

    return this.http.post(url, JSON.stringify(contractInfo)).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('contract.error.approveFailed')))
    );
  }
}
