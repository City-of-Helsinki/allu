import {combineLatest} from 'rxjs';
import {MatLegacyPaginator as MatPaginator} from '@angular/material/legacy-paginator';
import {MatSort} from '@angular/material/sort';
import {Customer} from '@model/customer/customer';
import {filter, takeUntil} from 'rxjs/operators';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromCustomer from '@feature/customerregistry/reducers';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Search, SetPaging, SetSort} from '@feature/customerregistry/actions/customer-search-actions';
import {StoreDatasource} from '@service/common/store-datasource';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';

export class CustomerDatasource extends StoreDatasource<Customer> {

  constructor(store: Store<fromRoot.State>,
              paginator: MatPaginator,
              sort: MatSort) {
    super(store, paginator, sort);
  }

  protected initSelectors(): void {
    this.actionTargetType = ActionTargetType.Customer;
  }

  protected initTargetType(): void {
    this.resultSelector = fromCustomer.getMatchingCustomers;
    this.sortSelector = fromCustomer.getCustomerSort;
    this.pageRequestSelector = fromCustomer.getCustomerPageRequest;
    this.searchingSelector = fromCustomer.getCustomersLoading;
  }

  protected dispatchPageRequest(pageRequest: PageRequest): void {
    this.store.dispatch(new SetPaging(this.actionTargetType, pageRequest));
  }

  protected dispatchSort(sort: Sort): void {
    this.store.dispatch(new SetSort(this.actionTargetType, sort));
  }

  protected setupSearch(): void {
    combineLatest([
      this.store.pipe(select(fromCustomer.getCustomerSearch), filter(search => !!search)),
      this.store.pipe(select(fromCustomer.getCustomerSort)),
      this.store.pipe(select(fromCustomer.getCustomerPageRequest))
    ]).pipe(
      takeUntil(this.destroy)
    ).subscribe(([query, sort, pageRequest]) =>
      this.store.dispatch(new Search(ActionTargetType.Customer, {query, sort, pageRequest})));
  }
}
