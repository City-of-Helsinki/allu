import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Router} from '@angular/router';
import {CustomerHub} from '../../service/customer/customer-hub';
import {FormBuilder, FormGroup} from '@angular/forms';
import {EnumUtil} from '../../util/enum.util';
import {CustomerType} from '../../model/customer/customer-type';
import {Sort} from '../../model/common/sort';
import {Customer} from '../../model/customer/customer';

@Component({
  selector: 'customer-list',
  templateUrl: './customer-list.component.html',
  styleUrls: [
    './customer-list.component.scss'
  ]
})
export class CustomerListComponent implements OnInit {

  customers: Observable<Array<Customer>>;
  searchForm: FormGroup;
  customerTypes = EnumUtil.enumValues(CustomerType);

  constructor(private router: Router, private customerHub: CustomerHub, private fb: FormBuilder) {
    this.searchForm = this.fb.group({
      name: [''],
      registryKey: [''],
      type: [''],
      active: [true],
      sort: [new Sort()]
    });
  }

  ngOnInit(): void {
    this.customers = this.customerHub.searchCustomersBy(this.searchForm.value);
  }

  newCustomer(): void {
    this.router.navigate(['customers/new']);
  }

  onSelect(customer: Customer): void {
    this.router.navigate(['customers', customer.id]);
  }

  search(): void {
    this.customers = this.customerHub.searchCustomersBy(this.searchForm.value);
  }

  sortBy(sort: Sort) {
    this.searchForm.patchValue({sort: sort});
    this.search();
  }
}
