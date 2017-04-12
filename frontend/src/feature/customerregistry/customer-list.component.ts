import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {Applicant} from '../../model/application/applicant/applicant';
import {Router} from '@angular/router';
import {CustomerHub} from '../../service/customer/customer-hub';

@Component({
  selector: 'customer-list',
  template: require('./customer-list.component.html'),
  styles: []
})
export class CustomerListComponent implements OnInit {

  customers: Observable<Array<Applicant>>;

  constructor(private router: Router, private customerHub: CustomerHub) {}

  ngOnInit(): void {
    this.customers = this.customerHub.fetchAllApplicants();
  }

  newCustomer(): void {
    this.router.navigate(['customers/new']);
  }

  onSelect(customer: Applicant): void {
    this.router.navigate(['customers', customer.id]);
  }
}
