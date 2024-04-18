import {Component, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {EnumUtil} from '@util/enum.util';
import {CustomerType} from '@model/customer/customer-type';
import {Customer} from '@model/customer/customer';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {CustomerDatasource} from '@service/customer/customer-datasource';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromCustomer from '@feature/customerregistry/reducers';
import {SetSearchQuery} from '@feature/customerregistry/actions/customer-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {filter, take} from 'rxjs/operators';

@Component({
  selector: 'customer-list',
  templateUrl: './customer-list.component.html',
  styleUrls: [
    './customer-list.component.scss'
  ]
})
export class CustomerListComponent implements OnInit {

  displayedColumns = ['name', 'type', 'registryKey', 'email', 'phone', 'postalAddress'];

  searchForm: UntypedFormGroup;
  customerTypes = EnumUtil.enumValues(CustomerType);
  customerSource: CustomerDatasource;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  constructor(private router: Router,
              private store: Store<fromRoot.State>,
              private fb: UntypedFormBuilder) {
    this.searchForm = this.fb.group({
      name: [''],
      registryKey: [''],
      type: [''],
      active: [true]
    });
  }

  ngOnInit(): void {
    this.customerSource = new CustomerDatasource(this.store, this.paginator, this.sort);

    this.store.pipe(
      select(fromCustomer.getCustomerSearch),
      take(1),
      filter(search => !!search)
    ).subscribe(search => this.searchForm.patchValue(search));
  }

  newCustomer(): void {
    this.router.navigate(['customers/new']);
  }

  search(): void {
    this.store.dispatch(new SetSearchQuery(ActionTargetType.Customer, this.searchForm.value));
  }

  trackById(index: number, item: Customer) {
    return item.id;
  }
}
