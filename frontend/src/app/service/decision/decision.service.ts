import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Decision} from '../../model/decision/Decision';
import {ErrorHandler} from '../error/error-handler.service';
import {DecisionDetails} from '../../model/decision/decision-details';
import {findTranslation} from '../../util/translations';
import {DecisionDetailsMapper} from '../mapper/decision-details-mapper';
import {catchError, map} from 'rxjs/internal/operators';

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

  public sendDecision(applicationId: number, emailDetails: DecisionDetails): Observable<{}> {
    const url = DECISION_DISTRIBUTION_URL.replace(':appId', String(applicationId));
    return this.sendToUrl(url, emailDetails);
  }

  public sendWorkFinished(applicationId: number, emailDetails: DecisionDetails): Observable<{}> {
    const url = WORK_FINISHED_DISTRIBUTION_URL.replace(':appId', String(applicationId));
    return this.sendToUrl(url, emailDetails);
  }

  public sendOperationalCondition(applicationId: number, emailDetails: DecisionDetails): Observable<{}> {
    const url = OPERATIONAL_CONDITION_DISTRIBUTION_URL.replace(':appId', String(applicationId));
    return this.sendToUrl(url, emailDetails);
  }

  public sendTermination(applicationId: number, emailDetails: DecisionDetails): Observable<{}> {
    const url = TERMINATION_DISTRIBUTION_URL.replace(':appId', String(applicationId));
    return this.sendToUrl(url, emailDetails);
  }

  private sendToUrl(url: string, emailDetails: DecisionDetails): Observable<{}> {
    return this.http.post(url, JSON.stringify(DecisionDetailsMapper.mapFrontend(emailDetails))).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('decision.error.send')))
    );
  }
}
