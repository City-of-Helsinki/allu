import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {DecisionHub} from './decision-hub';
import {UIStateHub} from '../ui-state/ui-state-hub';
import {Decision} from '../../model/decision/Decision';

@Injectable()
export class DecisionService {

  static DECISION_URL = '/api/applications/{id}/decision';

  constructor(private authHttp: AuthHttp, private decisionHub: DecisionHub, private uiState: UIStateHub) {
    decisionHub.generateRequest().subscribe(applicationId => this.generate(applicationId));
    decisionHub.fetchRequest().subscribe(applicationId => this.fetch(applicationId));
  }

  private generate(applicationId: number) {
    console.log('Generating pdf for application ' + applicationId);
    let url = DecisionService.DECISION_URL.replace('{id}', String(applicationId));

    this.authHttp.put(url, '').subscribe(
      result => {
        console.log('Generate decision pdf succeeded');
        this.fetch(applicationId);
      },
      error => this.uiState.addError('Generate decision pdf failed')
    );
  }


  private fetch(applicationId: number) {
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

    xhr.onreadystatechange = () => {
      if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
        let pdf = new Blob([xhr.response], {type: 'application/pdf'});
        this.decisionHub.addDecisions([new Decision(applicationId, pdf)]);
      }
    };

    xhr.send();
  }

}
