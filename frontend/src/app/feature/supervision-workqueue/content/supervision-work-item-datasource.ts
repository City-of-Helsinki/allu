import {DataSource} from '@angular/cdk/collections';
import {SupervisionWorkItem} from '@model/application/supervision/supervision-work-item';
import {Observable, Subject} from 'rxjs';
import {MatPaginator, MatSort} from '@angular/material';
import {SupervisionWorkItemStore} from '../supervision-work-item-store';
import {Sort} from '@model/common/sort';
import {Page} from '@model/common/page';
import {PageRequest} from '@model/common/page-request';
import {distinctUntilChanged, map, takeUntil} from 'rxjs/internal/operators';

export class SupervisionWorkItemDatasource extends DataSource<any> {
  private destroy = new Subject<boolean>();

  constructor(private store: SupervisionWorkItemStore, private paginator: MatPaginator, private sort: MatSort) {
    super();
  }

  connect(): Observable<SupervisionWorkItem[]> {
    // Initial paging
    this.store.pageRequestChange(new PageRequest(this.paginator.pageIndex, this.paginator.pageSize));

    this.sort.sortChange.pipe(takeUntil(this.destroy))
      .subscribe(sortChange => this.store.sortChange(Sort.fromMatSort(sortChange)));

    this.paginator.page.pipe(takeUntil(this.destroy))
      .subscribe(p => this.store.pageRequestChange(new PageRequest(p.pageIndex, p.pageSize)));

    return this.data;
  }

  disconnect(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  public get page(): Observable<Page<SupervisionWorkItem>> {
    return this.store.changes.pipe(
      takeUntil(this.destroy),
      map(state => state.page),
      distinctUntilChanged()
    );
  }

  public get data(): Observable<SupervisionWorkItem[]> {
    return this.page.pipe(map(page => page.content));
  }
}
