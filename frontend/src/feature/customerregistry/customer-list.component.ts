import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {Applicant} from '../../model/application/applicant/applicant';
import {Router} from '@angular/router';
import {CustomerHub} from '../../service/customer/customer-hub';
import {FormBuilder, FormGroup} from '@angular/forms';
import {EnumUtil} from '../../util/enum.util';
import {ApplicantType} from '../../model/application/applicant/applicant-type';
import {StringUtil} from '../../util/string.util';

@Component({
  selector: 'customer-list',
  template: require('./customer-list.component.html'),
  styles: [
    require('./customer-list.component.scss')
  ]
})
export class CustomerListComponent implements OnInit {

  customers: Observable<Array<Applicant>>;
  searchForm: FormGroup;
  applicantTypes = EnumUtil.enumValues(ApplicantType);

  constructor(private router: Router, private customerHub: CustomerHub, private fb: FormBuilder) {
    this.searchForm = this.fb.group({
      name: [''],
      type: ['']
    });
  }

  ngOnInit(): void {
    this.customers = this.customerHub.fetchAllApplicants();
  }

  newCustomer(): void {
    this.router.navigate(['customers/new']);
  }

  onSelect(customer: Applicant): void {
    this.router.navigate(['customers', customer.id]);
  }

  search(): void {
    let value = this.searchForm.value;
    if (StringUtil.isEmpty(value.name) && StringUtil.isEmpty(value.type)) {
      this.customers = this.customerHub.fetchAllApplicants();
    } else {
      this.customers = this.customerHub.searchApplicantsBy(this.searchForm.value);
    }
  }
}
