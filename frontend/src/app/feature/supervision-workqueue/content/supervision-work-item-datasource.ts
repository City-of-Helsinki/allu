import {DataSource} from '@angular/cdk/collections';
import {SupervisionWorkItem} from '../../../model/application/supervision/supervision-work-item';
import {Observable} from 'rxjs/Observable';
import {MatPaginator, MatSort} from '@angular/material';
import {SupervisionWorkItemStore} from '../supervision-work-item-store';
import {Sort} from '../../../model/common/sort';
import {Subject} from 'rxjs/Subject';
import {Page} from '../../../model/common/page';
import {PageRequest} from '../../../model/common/page-request';

export class SupervisionWorkItemDatasource extends DataSource<any> {

  private destroy = new Subject<boolean>();

  constructor(private store: SupervisionWorkItemStore, private paginator: MatPaginator, private sort: MatSort) {
    super();
  }

  connect(): Observable<SupervisionWorkItem[]> {
    this.sort.sortChange
      .takeUntil(this.destroy)
      .subscribe(sortChange => this.store.sortChange(Sort.fromMatSort(sortChange)));

    this.paginator.page
      .takeUntil(this.destroy)
      .subscribe(p => this.store.pageRequestChange(new PageRequest(p.pageIndex, p.pageSize)));

    return this.data;
  }

  disconnect(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  public get page(): Observable<Page<SupervisionWorkItem>> {
    return this.store.changes
      .takeUntil(this.destroy)
      .map(state => state.page).distinctUntilChanged();
  }

  public get data(): Observable<SupervisionWorkItem[]> {
    return this.page.map(page => page.content);
  }
}
