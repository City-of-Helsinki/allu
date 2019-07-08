import {DataSource} from '@angular/cdk/collections';
import {Observable, Subject, combineLatest} from 'rxjs';
import {MatPaginator, MatSort} from '@angular/material';
import {Application} from '@model/application/application';
import {ApplicationSearchQuery} from '@model/search/ApplicationSearchQuery';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';
import {map, take, takeUntil, tap} from 'rxjs/operators';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Search} from '@feature/application/actions/application-search-actions';


export class ApplicationSearchDatasource extends DataSource<Application> {

  public loading = false;
  private destroy = new Subject<boolean>();
  private searchQuery = new Subject<ApplicationSearchQuery>();

  constructor(private store: Store<fromRoot.State>,
              private targetType: ActionTargetType,
              private paginator: MatPaginator,
              private sort: MatSort) {
    super();
    this.setupSearch();
    this.setupInitialValues();
    this.handleLoadingChanges();
  }

  connect(): Observable<Application[]> {
    return this.store.pipe(
      select(fromApplication.getMatchingApplications),
      takeUntil(this.destroy),
      tap(page => this.updatePaginatorInfo(page.totalElements)),
      map(page => page.content)
    );
  }

  disconnect(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  searchChange(search: ApplicationSearchQuery) {
    this.searchQuery.next(search);
    this.paginator.firstPage();
  }

  private setupSearch(): void {
    combineLatest(
      this.searchQuery,
      this.sortChanges(),
      this.pagingChanges()
    ).subscribe(([search, sort, paging]) => {
      this.store.dispatch(new Search(ActionTargetType.Application, search, sort, paging));
    });
  }

  private pagingChanges(): Observable<PageRequest> {
    return this.paginator.page.pipe(
      takeUntil(this.destroy),
      map(page => new PageRequest(page.pageIndex, page.pageSize))
    );
  }

  private sortChanges(): Observable<Sort> {
    return this.sort.sortChange.pipe(
      takeUntil(this.destroy),
      map(sort => Sort.fromMatSort(sort)),
      tap(() => this.paginator.firstPage())
    );
  }

  private setupInitialValues(): void {
    this.store.pipe(
      select(fromApplication.getApplicationSearchPageRequest),
      take(1)
    ).subscribe(pageRequest => {
      this.paginator.pageIndex = pageRequest.page;
      // Need to use underscore notation so paginator emits a page change event after setting
      // can be replaced with this.paginator.pageSize = pageRequest.size when it is fixed
      this.paginator._changePageSize(pageRequest.size);
    });

    this.store.pipe(
      select(fromApplication.getApplicationSearchSort),
      take(1),
      map(sort => Sort.toMatSortable(sort))
    ).subscribe(sort => {
      this.sort.sort(sort);
    });
  }

  private handleLoadingChanges(): void {
    this.store.pipe(
      select(fromApplication.getSearchingApplications),
      takeUntil(this.destroy)
    ).subscribe(loading => this.loading = loading);
  }

  private updatePaginatorInfo(totalElements: number): void {
    this.paginator.length = totalElements;
  }
}
