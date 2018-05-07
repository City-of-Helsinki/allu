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

  public loading = false;

  private searchChanges = new Subject<ApplicationSearchQuery>();
  private destroy = new Subject<boolean>();
  private _page: Observable<Page<Application>>;
  private _pageSnapshot = new Page<Application>();
  private _search: ApplicationSearchQuery;

  constructor(private applicationService: ApplicationService,
              private notification: NotificationService,
              private paginator: MatPaginator,
              private sort: MatSort) {
    super();
  }

  connect(): Observable<Application[]> {
    this._page = this.pageChanges();
    this.resetPageIndexOnSearchSortChange();
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

  get pageSnapshot(): Page<Application> {
    return this._pageSnapshot;
  }

  private pageChanges(): Observable<Page<Application>> {
    const displayDataChanges = [
      this.searchChanges,
      this.sort.sortChange,
      this.paginator.page
    ];

    return Observable.merge(...displayDataChanges)
      .takeUntil(this.destroy)
      .skipUntil(this.searchChanges)
      .switchMap(() => this.load())
      .do(page => {
        this._pageSnapshot = page;
        this.loading = false;
      });
  }

  private load(): Observable<Page<Application>> {
    this.loading = true;
    return this.applicationService.pagedSearch(
      this._search,
      new Sort(this.sort.active, this.sort.direction),
      new PageRequest(this.paginator.pageIndex, this.paginator.pageSize))
    .catch(err => {
      this.notification.errorInfo(err);
      return Observable.of(new Page<Application>());
    });
  }

  private resetPageIndexOnSearchSortChange(): void {
    const changes = [
      this.searchChanges,
      this.sort.sortChange,
    ];
    Observable.merge(...changes)
      .takeUntil(this.destroy)
      .subscribe(() => this.paginator.pageIndex = 0);
  }
}
