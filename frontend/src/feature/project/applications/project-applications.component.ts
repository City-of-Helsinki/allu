import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';
import {Application} from '../../../model/application/application';
import {Project} from '../../../model/project/project';
import {ApplicationSearchQuery} from '../../../model/search/ApplicationSearchQuery';
import {ApplicationHub} from '../../../service/application/application-hub';
import {ContentRow} from '../../../model/common/content-row';
import {Sort} from '../../../model/common/sort';
import {UI_PIPE_DATE_FORMAT} from '../../../util/time.util';
import {ProjectState} from '../../../service/project/project-state';
import {NotificationService} from '../../../service/notification/notification.service';


@Component({
  selector: 'project-applications',
  template: require('./project-applications.component.html'),
  styles: []
})
export class ProjectApplicationsComponent implements OnInit {

  project: Project;
  applicationRows: Array<ContentRow<Application>> = [];
  applicationSearch = new Subject<string>();
  matchingApplications: Observable<Array<Application>>;
  allSelected = false;
  sort: Sort = new Sort(undefined, undefined);
  dateFormat = UI_PIPE_DATE_FORMAT;

  constructor(private router: Router, private applicationHub: ApplicationHub,
              private projectState: ProjectState) {}

  ngOnInit(): void {
    this.project = this.projectState.project;
    this.getProjectApplications()
      .subscribe(rows => this.applicationRows = rows);

    this.matchingApplications = this.applicationSearch.asObservable()
      .debounceTime(300)
      .distinctUntilChanged()
      .map(idSearch => ApplicationSearchQuery.forApplicationId(idSearch))
      .switchMap(search => this.applicationHub.searchApplications(search))
      .catch(err => NotificationService.errorCatch(err, []));
  }

  checkAll() {
    let selection = !this.allSelected;
    this.applicationRows.forEach(row => row.selected = selection);
  }

  checkSingle(row: ContentRow<Application>) {
    row.selected = !row.selected;
    this.updateAllSelected();
  }

  goToSummary(col: number, row: ContentRow<Application>): void {
    // undefined and 0 should not trigger navigation
    if (col) {
      this.router.navigate(['applications', row.id, 'summary']);
    }
  }

  add(application: Application) {
    let rows = this.applicationRows.concat(new ContentRow(application));
    this.updateApplications(rows);
  }

  remove() {
    let rows = this.applicationRows.filter(row => !row.selected);
    this.updateApplications(rows);
  }

  onIdentifierSearchChange(identifier: string) {
    this.applicationSearch.next(identifier);
  }

  sortBy(sort: Sort) {
    this.sort = sort;
    this.applicationRows = this.sortRows(this.sort, this.applicationRows);
  }

  private getProjectApplications(): Observable<Array<ContentRow<Application>>> {
    return this.projectState.applications
      .map(applications => applications.map(app => new ContentRow(app)));
  }

  private updateApplications(rows: Array<ContentRow<Application>>): void {
    this.projectState.updateApplications(rows.map(app => app.id))
      .subscribe(p => this.project = p);
  }

  private updateAllSelected(): void {
    this.allSelected = this.applicationRows.every(row => row.selected);
  }

  private sortRows(sort: Sort, rows: Array<ContentRow<Application>>): Array<ContentRow<Application>> {
    let original = rows;
    let sorted =  rows
      .map(row => row.content)
      .sort(sort.sortFn())
      .map(app => new ContentRow(app));

    return sort.byDirection(original, sorted);
  }
}
