import {Injectable} from '@angular/core';
import {Subject} from 'rxjs';
import {Applicant} from '../../../model/application/applicant/applicant';

@Injectable()
export class ApplicantEvents {
  private applicantChange$ = new Subject<Applicant>();

  get applicantChange() {
    return this.applicantChange$.asObservable();
  }

  public emitApplicantChange = (applicant: Applicant) => this.applicantChange$.next(applicant);
}
