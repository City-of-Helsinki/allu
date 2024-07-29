import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Decision} from '../../model/decision/Decision';
import {ErrorHandler} from '../error/error-handler.service';
import {DecisionDetails} from '../../model/decision/decision-details';
import {findTranslation} from '../../util/translations';
import {DecisionDetailsMapper} from '../mapper/decision-details-mapper';
import {catchError, map} from 'rxjs/internal/operators';
import {BulkApprovalEntry} from '@app/model/decision/bulk-approval-entry';
import {BackendBulkApprovalEntry, BulkApprovalEntryMapper} from '../mapper/bulk-approval-entry-mapper';
import {ApplicationStatus} from '@model/application/application-status';

const DECISION_URL = '/api/applications/:appId/decision';
const DECISION_DISTRIBUTION_URL = '/api/applications/:appId/decision/send';
const WORK_FINISHED_DISTRIBUTION_URL = '/api/applications/:appId/work_finished/send';
const OPERATIONAL_CONDITION_DISTRIBUTION_URL = '/api/applications/:appId/operational_condition/send';
const TERMINATION_DISTRIBUTION_URL = '/api/applications/:appId/termination/send';

@Injectable()
export class DecisionService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  public fetch(applicationId: number): Observable<Decision> {
    const url = DECISION_URL.replace(':appId', String(applicationId));

    return this.http.get(url, {responseType: 'blob'}).pipe(
      map(pdf => new Decision(applicationId, pdf))
    );
  }

  public sendDecision(applicationId: number, emailDetails: DecisionDetails): Observable<object> {
    const url = DECISION_DISTRIBUTION_URL.replace(':appId', String(applicationId));
    return this.sendToUrl(url, emailDetails);
  }

  public sendWorkFinished(applicationId: number, emailDetails: DecisionDetails): Observable<object> {
    const url = WORK_FINISHED_DISTRIBUTION_URL.replace(':appId', String(applicationId));
    return this.sendToUrl(url, emailDetails);
  }

  public sendOperationalCondition(applicationId: number, emailDetails: DecisionDetails): Observable<object> {
    const url = OPERATIONAL_CONDITION_DISTRIBUTION_URL.replace(':appId', String(applicationId));
    return this.sendToUrl(url, emailDetails);
  }

  public sendTermination(applicationId: number, emailDetails: DecisionDetails): Observable<object> {
    const url = TERMINATION_DISTRIBUTION_URL.replace(':appId', String(applicationId));
    return this.sendToUrl(url, emailDetails);
  }

  public sendByStatus(applicationId: number, status: ApplicationStatus, emailDetails: DecisionDetails): Observable<object> {
    switch (status) {
      case ApplicationStatus.OPERATIONAL_CONDITION: {
        return this.sendOperationalCondition(applicationId, emailDetails);
      }
      case ApplicationStatus.FINISHED: {
        return this.sendWorkFinished(applicationId, emailDetails);
      }
      case ApplicationStatus.TERMINATED: {
        return this.sendTermination(applicationId, emailDetails);
      }
      default: {
        return this.sendDecision(applicationId, emailDetails);
      }
    }
  }

  public getBulkApprovalEntries(applicationIds: number[]): Observable<BulkApprovalEntry[]> {
    const url = `/api/applications/bulkApprovalEntries`;
    return this.http.post<BackendBulkApprovalEntry[]>(url, JSON.stringify(applicationIds)).pipe(
      map(bulkApprovalEntries => BulkApprovalEntryMapper.mapBackendList(bulkApprovalEntries)),
      catchError(error => this.errorHandler.handle(error, findTranslation('decision.error.fetchBulkApprovalEntries')))
    );
  }

  private sendToUrl(url: string, emailDetails: DecisionDetails): Observable<object> {
    if (emailDetails.decisionDistributionList && emailDetails.decisionDistributionList.length) {
      return this.http.post(url, JSON.stringify(DecisionDetailsMapper.mapFrontend(emailDetails))).pipe(
        catchError(error => this.errorHandler.handle(error, findTranslation('decision.error.send')))
      );
    } else {
      return of({});
    }
  }
}
