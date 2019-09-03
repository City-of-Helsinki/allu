import {DataSource} from '@angular/cdk/collections';
import {merge, Observable, of, Subject} from 'rxjs';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {ProjectService} from './project.service';
import {Project} from '../../model/project/project';
import {ProjectSearchQuery} from '../../model/project/project-search-query';
import {Sort} from '../../model/common/sort';
import {PageRequest} from '../../model/common/page-request';
import {Page} from '../../model/common/page';
import {NotificationService} from '../../feature/notification/notification.service';
import {catchError, map, skipUntil, switchMap, takeUntil, tap} from 'rxjs/internal/operators';

export class ProjectSearchDatasource extends DataSource<any> {

  public loading = false;

  private searchChanges = new Subject<ProjectSearchQuery>();
  private destroy = new Subject<boolean>();
  private _page: Observable<Page<Project>>;
  private _pageSnapshot = new Page<Project>();
  private _search: ProjectSearchQuery;

  constructor(private projectService: ProjectService,
              private notification: NotificationService,
              private paginator: MatPaginator,
              private sort: MatSort) {
    super();
  }

  connect(): Observable<Project[]> {
    this._page = this.pageChanges();
    this.resetPageIndexOnSearchSortChange();
    return this.data;
  }

  disconnect(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  searchChange(search: ProjectSearchQuery) {
    this._search = search;
    this.searchChanges.next(search);
  }

  get data(): Observable<Project[]> {
    return this._page.pipe(map(page => page.content));
  }

  get pageSnapshot(): Page<Project> {
    return this._pageSnapshot;
  }

  private pageChanges(): Observable<Page<Project>> {
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

  private load(): Observable<Page<Project>> {
    this.loading = true;
    return this.projectService.pagedSearch(
      this._search,
      new Sort(this.sort.active, this.sort.direction),
      new PageRequest(this.paginator.pageIndex, this.paginator.pageSize)).pipe(
      catchError(err => {
        this.notification.errorInfo(err);
        return of(new Page<Project>());
      })
    );
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
