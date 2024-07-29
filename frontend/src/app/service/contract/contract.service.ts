import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {catchError, map} from 'rxjs/internal/operators';
import {ErrorHandler} from '@service/error/error-handler.service';
import {Contract} from '@model/contract/contract';
import {findTranslation} from '@util/translations';
import {ContractApprovalInfo} from '@model/decision/contract-approval-info';
import {ContractApprovalInfoMapper} from '@service/mapper/contract-approval-info-mapper';

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

  public createProposal(applicationId: number): Observable<Contract> {
    const url = `${URL_PREFIX}/${applicationId}/contract/proposal`;

    return this.http.post(url, null, {responseType: 'blob'}).pipe(
      map(pdf => new Contract(applicationId, pdf)),
      catchError(error => this.errorHandler.handle(error, findTranslation('contract.error.createProposalFailed')))
    );
  }

  public approve(applicationId: number, approvalInfo: ContractApprovalInfo): Observable<Contract> {
    const url = `${URL_PREFIX}/${applicationId}/contract/approved`;

    return this.http.post(url, JSON.stringify(ContractApprovalInfoMapper.mapFrontEnd(approvalInfo)), {responseType: 'blob'}).pipe(
      map(pdf => new Contract(applicationId, pdf)),
      catchError(error => this.errorHandler.handle(error, findTranslation('contract.error.approveFailed')))
    );
  }

  public reject(applicationId: number, reason: string): Observable<object> {
    const url = `${URL_PREFIX}/${applicationId}/contract/rejected`;
    return this.http.post<object>(url, reason).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('contract.error.rejectFailed')))
    );
  }
}
