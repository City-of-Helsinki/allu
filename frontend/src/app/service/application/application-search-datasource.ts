import {DataSource} from '@angular/cdk/collections';
import {Subject} from 'rxjs/Subject';
import {MatPaginator, MatSort} from '@angular/material';
import {ApplicationService} from './application.service';
import {Observable} from 'rxjs/Observable';
import {Application} from '../../model/application/application';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {Sort} from '../../model/common/sort';
import {PageRequest} from '../../model/common/page-request';
import {Page} from '../../model/common/page';
import {NotificationService} from '../notification/notification.service';
import '../../rxjs-extensions';

export class ApplicationSearchDatasource extends DataSource<any> {

  private searchChanges = new Subject<ApplicationSearchQuery>();
  private destroy = new Subject<boolean>();
  private _page: Observable<Page<Application>>;
  private _search: ApplicationSearchQuery;
  private _length = 0;

  constructor(private applicationService: ApplicationService, private paginator: MatPaginator, private sort: MatSort) {
    super();
  }

  connect(): Observable<Application[]> {
    const displayDataChanges = [
      this.searchChanges,
      this.sort.sortChange,
      this.paginator.page
    ];

    this._page = Observable.merge(...displayDataChanges)
      .takeUntil(this.destroy)
      .skipUntil(this.searchChanges)
      .switchMap(change => this.applicationService.pagedSearch(
        this._search,
        new Sort(this.sort.active, this.sort.direction),
        new PageRequest(this.paginator.pageIndex, this.paginator.pageSize)
      )).catch(err => {
        NotificationService.error(err);
        return Observable.of(new Page<Application>());
      }).do(page => this._length = page.totalElements);

    return this.data;
  }

  disconnect(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  searchChange(search: ApplicationSearchQuery) {
    this._search = search;
    this.searchChanges.next(search);
  }

  get data(): Observable<Application[]> {
    return this._page.map(page => page.content);
  }

  get length(): number {
    return this._length;
  }
}
