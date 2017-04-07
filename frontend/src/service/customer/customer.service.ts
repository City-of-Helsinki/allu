import {Injectable} from '@angular/core';
import {ErrorHandler} from '../error/error-handler.service';
import {AuthHttp} from 'angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {Applicant} from '../../model/application/applicant';
import {ApplicantMapper} from '../mapper/applicant-mapper';
import {findTranslation} from '../../util/translations';

const APPLICANTS_URL = '/api/applicants';
const APPLICANTS_SEARCH_URL = APPLICANTS_URL + '/search/:fieldName';

@Injectable()
export class CustomerService {

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
  }

  public searchApplicantsByField(fieldName: string, term: string): Observable<Array<Applicant>> {
    let url = APPLICANTS_SEARCH_URL.replace(':fieldName', String(fieldName));
    return this.authHttp.post(url, term)
      .map(response => response.json())
      .map(applicants => applicants.map(a => ApplicantMapper.mapBackend(a)))
      .catch(error => this.errorHandler.handle(error, findTranslation('applicant.error.fetch')));
  }
}
