import {Component, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {FormBuilder, FormGroup} from '@angular/forms';
import {EnumUtil} from '../../util/enum.util';
import {CustomerType} from '../../model/customer/customer-type';
import {Customer} from '../../model/customer/customer';
import {CustomerService} from '../../service/customer/customer.service';
import {MatPaginator, MatSort} from '@angular/material';
import {CustomerDatasource} from '../../service/customer/customer-datasource';

@Component({
  selector: 'customer-list',
  templateUrl: './customer-list.component.html',
  styleUrls: [
    './customer-list.component.scss'
  ]
})
export class CustomerListComponent implements OnInit {

  displayedColumns = ['name', 'type', 'registryKey', 'email', 'phone', 'postalAddress'];

  searchForm: FormGroup;
  customerTypes = EnumUtil.enumValues(CustomerType);
  customerSource: CustomerDatasource;

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private router: Router, private customerService: CustomerService, private fb: FormBuilder) {
    this.searchForm = this.fb.group({
      name: [''],
      registryKey: [''],
      type: [''],
      active: [true]
    });
  }

  ngOnInit(): void {
    this.customerSource = new CustomerDatasource(this.customerService, this.paginator, this.sort);
  }

  newCustomer(): void {
    this.router.navigate(['customers/new']);
  }

  search(): void {
    this.customerSource.searchChange(this.searchForm.value);
  }

  trackById(index: number, item: Customer) {
    return item.id;
  }
}
