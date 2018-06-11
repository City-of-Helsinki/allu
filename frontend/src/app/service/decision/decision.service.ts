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

@Injectable()
export class DecisionService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  public fetch(applicationId: number): Observable<Decision> {
    console.log('Fetching pdf for application ' + applicationId);
    const url = DECISION_URL.replace(':appId', String(applicationId));

    return this.http.get(url, {responseType: 'blob'}).pipe(
      map(pdf => new Decision(applicationId, pdf))
    );
  }

  public sendDecision(applicationId: number, emailDetails: DecisionDetails): Observable<{}> {
    const url = DECISION_DISTRIBUTION_URL.replace(':appId', String(applicationId));
    return this.http.post(url, JSON.stringify(DecisionDetailsMapper.mapFrontend(emailDetails))).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('decision.error.send')))
    );
  }
}
