import {Injectable} from '@angular/core';
import {ApplicantService} from './applicant.service';

@Injectable()
export class ApplicantHub {

  constructor(private applicantService: ApplicantService) {}

  /**
   * Fetches applicants by field and given search term
   */
  public searchApplicantsByField = (fieldName: string, term: string) => this.applicantService.searchApplicantsByField(fieldName, term);
}
