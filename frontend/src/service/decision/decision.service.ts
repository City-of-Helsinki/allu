import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {UIStateHub} from '../ui-state/ui-state-hub';
import {Decision} from '../../model/decision/Decision';
import {ErrorInfo} from './../ui-state/error-info';
import {ErrorType} from '../ui-state/error-type';
import '../../rxjs-extensions.ts';
import {ResponseContentType} from '@angular/http';
import {ErrorHandler} from '../error/error-handler.service';

const DECISION_URL = '/api/applications/:appId/decision';
const DECISION_PREVIEW_URL = '/api/applications/:appId/decision-preview';

@Injectable()
export class DecisionService {

  constructor(private authHttp: AuthHttp, private uiState: UIStateHub, private errorHandler: ErrorHandler) {
  }

  public generate(applicationId: number): Observable<Decision> {
    console.log('Generating pdf for application ' + applicationId);
    let url = DECISION_URL.replace(':appId', String(applicationId));

    return this.authHttp.put(url, '')
      .flatMap(decision => this.fetch(applicationId))
      .catch(error => this.uiState.addError(new ErrorInfo(ErrorType.PDF_GENERATION_FAILED)));
  }


  public fetch(applicationId: number): Observable<Decision> {
    console.log('Fetching pdf for application ' + applicationId);
    let url = DECISION_URL.replace(':appId', String(applicationId));

    return this.authHttp.get(url, {responseType: ResponseContentType.Blob})
      .map(response => response.blob())
      .map(pdf => new Decision(applicationId, pdf));
  }

  public preview(applicationId: number): Observable<Decision> {
    console.log('Fetching pdf preview for application ' + applicationId);
    let url = DECISION_PREVIEW_URL.replace(':appId', String(applicationId));

    return this.authHttp.get(url, {responseType: ResponseContentType.Blob})
      .map(response => response.blob())
      .map(pdf => new Decision(applicationId, pdf));
  }
}
