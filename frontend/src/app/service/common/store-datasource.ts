import {DataSource} from '@angular/cdk/collections';
import {Observable, Subject} from 'rxjs';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort, MatSortable} from '@angular/material/sort';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';
import {map, take, takeUntil, tap} from 'rxjs/operators';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

export abstract class StoreDatasource<T> extends DataSource<T> {

  public loading = false;

  protected actionTargetType: ActionTargetType;
  protected resultSelector;
  protected sortSelector;
  protected pageRequestSelector;
  protected searchingSelector;

  protected destroy = new Subject<boolean>();

  protected constructor(protected store: Store<fromRoot.State>,
                        protected paginator: MatPaginator,
                        protected sort: MatSort) {
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

  connect(): Observable<T[]> {
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

  setSort(sort: MatSortable): void {
    // Need to reset active to correctly set the sort.
    // If we would sort by same field it would pick next sort direction
    // instead of the one we give it inside sort
    this.sort.active = undefined;
    this.sort.sort(sort);
  }

  /**
   * Must be overridden for target type initialization logic
   */
  protected abstract initTargetType(): void;

  /**
   * Must be overridden for selector initialization logic
   */
  protected abstract initSelectors(): void;

  /**
   * Must be overridden for search setup functionality eg. when search is triggered
   * and what happens when it is.
   */
  protected abstract setupSearch(): void;


  /**
   * Must be overridden for dispatching sorting changes
   */
  protected abstract dispatchSort(sort: Sort): void;

  /**
   * Must be overridden for dispatching page request changes
   */
  protected abstract dispatchPageRequest(pageRequest: PageRequest): void;

  private handlePageChanges(): void {
    this.paginator.page.pipe(
      takeUntil(this.destroy),
      map(page => new PageRequest(page.pageIndex, page.pageSize))
    ).subscribe(pr => this.dispatchPageRequest(pr));
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
    ).subscribe(sort => this.dispatchSort(sort));
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
