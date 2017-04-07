import {Injectable} from '@angular/core';
import {CustomerService} from './customer.service';

@Injectable()
export class CustomerHub {

  constructor(private customerService: CustomerService) {}

  /**
   * Fetches applicants by field and given search term
   */
  public searchApplicantsByField = (fieldName: string, term: string) => this.customerService.searchApplicantsByField(fieldName, term);
}
