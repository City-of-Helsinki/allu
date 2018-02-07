import {DataSource} from '@angular/cdk/collections';
import {Observable} from 'rxjs/Observable';
import {MatPaginator, MatSort} from '@angular/material';
import {Sort} from '../../../model/common/sort';
import {Subject} from 'rxjs/Subject';
import {Page} from '../../../model/common/page';
import {PageRequest} from '../../../model/common/page-request';
import {ApplicationWorkItemStore} from '../application-work-item-store';
import {Application} from '../../../model/application/application';
import {Some} from '../../../util/option';
import {ApplicationTag} from '../../../model/application/tag/application-tag';
import {NotificationService} from '../../../service/notification/notification.service';

export interface ApplicationWorkItemRow {
  content: Application | ApplicationTag[];
  relatedIndex?: number;
}

export class ApplicationWorkItemDatasource extends DataSource<any> {

  private destroy = new Subject<boolean>();

  constructor(private store: ApplicationWorkItemStore, private paginator: MatPaginator, private sort: MatSort) {
    super();

    // Initial paging
    this.store.pageRequestChange(new PageRequest(this.paginator.pageIndex, this.paginator.pageSize));
  }

  connect(): Observable<ApplicationWorkItemRow[]> {
    this.sort.sortChange
      .takeUntil(this.destroy)
      .distinctUntilChanged()
      .subscribe(sortChange => this.store.sortChange(Sort.fromMatSort(sortChange)));

    this.paginator.page
      .takeUntil(this.destroy)
      .distinctUntilChanged()
      .subscribe(p => this.store.pageRequestChange(new PageRequest(p.pageIndex, p.pageSize)));

    // Material datatable when condition is not run properly if empty data is not provided
    // between data changes. To fix this we provide an empty array between all data changes.
    return this.data.mergeMap(d => Observable.of([], d));
  }

  disconnect(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  public get page(): Observable<Page<Application>> {
    return this.store.changes
      .takeUntil(this.destroy)
      .map(state => state.page).distinctUntilChanged()
      .catch(err => {
        NotificationService.error(err);
        return Observable.of(new Page<Application>());
      });
  }

  public get data(): Observable<ApplicationWorkItemRow[]> {
    return this.page.map(page => page.content)
      .map(content => this.toRows(content));
  }

  private toRows(applications: Application[]): ApplicationWorkItemRow[] {
    return applications.reduce((prev, cur) => {
      const tagRow = Some(cur.applicationTags)
        .filter(tags => tags.length > 0)
        .map((tags) => ({content: tags, relatedIndex: prev.length}));

      prev.push({content: cur, relatedIndex: tagRow.map(() => prev.length + 1).orElse(undefined)});
      tagRow.do(tags => prev.push(tags));

      return prev;
    }, []);
  }
}
