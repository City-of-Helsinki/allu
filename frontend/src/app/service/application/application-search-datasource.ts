import {DataSource} from '@angular/cdk/collections';
import {combineLatest, Observable, Subject} from 'rxjs';
import {MatPaginator, MatSort} from '@angular/material';
import {Application} from '@model/application/application';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';
import {filter, map, take, takeUntil, tap} from 'rxjs/operators';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Search, SetPaging, SetSort} from '@feature/application/actions/application-search-actions';
import {ApplicationSearchQuery} from '@model/search/ApplicationSearchQuery';

export class ApplicationSearchDatasource extends DataSource<Application> {

  public loading = false;

  protected actionTargetType: ActionTargetType = ActionTargetType.Application;
  protected resultSelector = fromApplication.getMatchingApplications;
  protected searchParametersSelector = fromApplication.getApplicationSearchParameters;
  protected sortSelector = fromApplication.getApplicationSearchSort;
  protected pageRequestSelector = fromApplication.getApplicationSearchPageRequest;
  protected searchingSelector = fromApplication.getSearchingApplications;

  private destroy = new Subject<boolean>();

  constructor(protected store: Store<fromRoot.State>,
              private paginator: MatPaginator,
              private sort: MatSort) {
    super();
    this.initTargetType();
    this.initSelectors();
    this.setupSearch();
    this.setupInitialValues();
    this.handleSortChanges();
    this.handlePageChanges();
    this.onLoadingChanges();
    this.onPageChanges();
  }

  connect(): Observable<Application[]> {
    return this.store.pipe(
      select(this.resultSelector),
      takeUntil(this.destroy),
      tap(page => this.updatePaginatorInfo(page.totalElements)),
      map(page => page.content)
    );
  }

  disconnect(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  /**
   * Can be overridden for target type initialization logic
   */
  protected initTargetType() {}

  /**
   * Can be overridden for selector initialization logic
   */
  protected initSelectors() {}

  /**
   * Can be overridden for getting search parameters
   */
  protected get searchParameters(): Observable<ApplicationSearchQuery> {
    return this.store.pipe(select(this.searchParametersSelector), filter(params => !!params));
  }

  private setupSearch(): void {
    combineLatest(
      this.searchParameters,
      this.store.pipe(select(this.sortSelector)),
      this.store.pipe(select(this.pageRequestSelector)),
    ).pipe(
      takeUntil(this.destroy)
    ).subscribe(([search, sort, paging]) => {
      this.store.dispatch(new Search(this.actionTargetType, search, sort, paging));
    });
  }

  private handlePageChanges(): void {
    this.paginator.page.pipe(
      takeUntil(this.destroy),
      map(page => new PageRequest(page.pageIndex, page.pageSize))
    ).subscribe(pr => this.store.dispatch(new SetPaging(this.actionTargetType, pr)));
  }

  private onPageChanges(): void {
    this.store.pipe(
      select(this.pageRequestSelector),
      takeUntil(this.destroy)
    ).subscribe(pageRequest => {
      this.paginator.pageIndex = pageRequest.page;
      this.paginator.pageSize = pageRequest.size;
    });
  }

  private handleSortChanges(): void {
    this.sort.sortChange.pipe(
      takeUntil(this.destroy),
      map(sort => Sort.fromMatSort(sort)),
    ).subscribe(sort => {
      this.store.dispatch(new SetSort(this.actionTargetType, sort));
    });
  }

  private setupInitialValues(): void {
    this.store.pipe(
      select(this.sortSelector),
      take(1),
      map(sort => Sort.toMatSortable(sort))
    ).subscribe(sort => {
      this.sort.sort(sort);
    });
  }

  private onLoadingChanges(): void {
    this.store.pipe(
      select(this.searchingSelector),
      takeUntil(this.destroy)
    ).subscribe(loading => this.loading = loading);
  }

  private updatePaginatorInfo(totalElements: number): void {
    this.paginator.length = totalElements;
  }
}
