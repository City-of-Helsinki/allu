import {DataSource} from '@angular/cdk/collections';
import {merge, Observable, of, Subject} from 'rxjs';
import {MatPaginator, MatSort} from '@angular/material';
import {Sort} from '../../model/common/sort';
import {PageRequest} from '../../model/common/page-request';
import {Page} from '../../model/common/page';
import {NotificationService} from '../../feature/notification/notification.service';
import {CustomerService} from './customer.service';
import {Customer} from '../../model/customer/customer';
import {CustomerSearchQuery} from './customer-search-query';
import {catchError, map, skipUntil, switchMap, takeUntil, tap} from 'rxjs/internal/operators';

export class CustomerDatasource extends DataSource<any> {

  public loading = false;

  private searchChanges = new Subject<CustomerSearchQuery>();
  private destroy = new Subject<boolean>();
  private _page: Observable<Page<Customer>>;
  private _search: CustomerSearchQuery;
  private _pageSnapshot = new Page<Customer>();

  constructor(private service: CustomerService,
              private notification: NotificationService,
              private paginator: MatPaginator,
              private sort: MatSort) {
    super();
  }

  connect(): Observable<Customer[]> {
    this._page = this.pageChanges();
    this.resetPageIndexOnSearchSortChange();
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
    return this._page.pipe(map(page => page.content));
  }

  get pageSnapshot(): Page<Customer> {
    return this._pageSnapshot;
  }

  private pageChanges(): Observable<Page<Customer>> {
    const displayDataChanges = [
      this.searchChanges,
      this.sort.sortChange,
      this.paginator.page
    ];

    return merge(...displayDataChanges).pipe(
      takeUntil(this.destroy),
      skipUntil(this.searchChanges),
      switchMap(() => this.load()),
      tap(page => {
        this._pageSnapshot = page;
        this.loading = false;
      })
    );
  }

  private load(): Observable<Page<Customer>> {
    this.loading = true;
    return this.service.pagedSearch(
      this._search,
      new Sort(this.sort.active, this.sort.direction),
      new PageRequest(this.paginator.pageIndex, this.paginator.pageSize)
    ).pipe(
      catchError(err => {
        this.notification.errorInfo(err);
        return of(new Page<Customer>());
    }));
  }

  private resetPageIndexOnSearchSortChange(): void {
    const changes = [
      this.searchChanges,
      this.sort.sortChange,
    ];
    merge(...changes).pipe(
      takeUntil(this.destroy)
    ).subscribe(() => this.paginator.pageIndex = 0);
  }
}
