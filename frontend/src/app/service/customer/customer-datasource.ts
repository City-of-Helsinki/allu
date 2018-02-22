import {DataSource} from '@angular/cdk/collections';
import {Subject} from 'rxjs/Subject';
import {MatPaginator, MatSort} from '@angular/material';
import {Observable} from 'rxjs/Observable';
import {Sort} from '../../model/common/sort';
import {PageRequest} from '../../model/common/page-request';
import {Page} from '../../model/common/page';
import {NotificationService} from '../notification/notification.service';
import '../../rxjs-extensions';
import {CustomerService} from './customer.service';
import {Customer} from '../../model/customer/customer';
import {CustomerSearchQuery} from './customer-search-query';

export class CustomerDatasource extends DataSource<any> {

  private searchChanges = new Subject<CustomerSearchQuery>();
  private destroy = new Subject<boolean>();
  private _page: Observable<Page<Customer>>;
  private _search: CustomerSearchQuery;
  private _length = 0;

  constructor(private service: CustomerService, private paginator: MatPaginator, private sort: MatSort) {
    super();
  }

  connect(): Observable<Customer[]> {
    const displayDataChanges = [
      this.searchChanges,
      this.sort.sortChange,
      this.paginator.page
    ];

    this._page = Observable.merge(...displayDataChanges)
      .takeUntil(this.destroy)
      .skipUntil(this.searchChanges)
      .switchMap(change => this.service.pagedSearch(
        this._search,
        new Sort(this.sort.active, this.sort.direction),
        new PageRequest(this.paginator.pageIndex, this.paginator.pageSize)
      )).catch(err => {
        NotificationService.error(err);
        return Observable.of(new Page<Customer>());
      }).do(page => this._length = page.totalElements);

    return this.data;
  }

  disconnect(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  searchChange(search: CustomerSearchQuery) {
    this._search = search;
    this.searchChanges.next(search);
  }

  get data(): Observable<Customer[]> {
    return this._page.map(page => page.content);
  }

  get length(): number {
    return this._length;
  }
}
