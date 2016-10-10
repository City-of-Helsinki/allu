import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';

import {UIStateHub} from '../ui-state/ui-state-hub';
import {Decision} from '../../model/decision/Decision';
import {ErrorInfo} from './../ui-state/error-info';
import {ErrorType} from '../ui-state/error-type';
import '../../rxjs-extensions.ts';

@Injectable()
export class DecisionService {

  static DECISION_URL = '/api/applications/{id}/decision';

  constructor(private authHttp: AuthHttp, private uiState: UIStateHub) {
  }

  public generate(applicationId: number): Observable<Decision> {
    console.log('Generating pdf for application ' + applicationId);
    let url = DecisionService.DECISION_URL.replace('{id}', String(applicationId));

    return this.authHttp.put(url, '')
      .flatMap(decision => this.fetch(applicationId))
      .catch(error => this.uiState.addError(new ErrorInfo(ErrorType.PDF_GENERATION_FAILED)));
  }


  public fetch(applicationId: number): Observable<Decision> {
    console.log('Fetching pdf for application ' + applicationId);
    /**********
     * TODO: replace direct use of XMLHttpRequest with Angular's Http (or AuthHttp) when it supports blobs:
     * https://github.com/angular/angular/pull/10190
     */

    let xhr = new XMLHttpRequest();
    let url = DecisionService.DECISION_URL.replace('{id}', String(applicationId));

    xhr.open('GET', url, true);
    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwt'));
    xhr.responseType = 'blob';

    let decisionSubject = new Subject<Decision>();

    xhr.onreadystatechange = () => {
      if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
        let pdf = new Blob([xhr.response], {type: 'application/pdf'});
        decisionSubject.next(new Decision(applicationId, pdf));
        decisionSubject.complete();
      }
    };

    xhr.send();
    return decisionSubject.asObservable();
  }

}
