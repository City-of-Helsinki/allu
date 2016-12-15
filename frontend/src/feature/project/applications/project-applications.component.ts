import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';

import {ProjectHub} from '../../../service/project/project-hub';
import {Application} from '../../../model/application/application';
import {Project} from '../../../model/project/project';
import {ApplicationSearchQuery} from '../../../model/search/ApplicationSearchQuery';
import {ApplicationHub} from '../../../service/application/application-hub';
import {ContentRow} from '../../../model/common/content-row';
import {Sort} from '../../../model/common/sort';
import {UI_DATE_FORMAT} from '../../../util/time.util';


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
  dateFormat = UI_DATE_FORMAT;

  constructor(private route: ActivatedRoute, private router: Router,
              private projectHub: ProjectHub, private applicationHub: ApplicationHub) {}

  ngOnInit(): void {
    this.route.data
      .map((data: {project: Project}) => data.project)
      .subscribe(project => {
        this.project = project;

        this.getProjectApplications().subscribe(rows => this.applicationRows = rows);
      });

    this.matchingApplications = this.applicationSearch.asObservable()
      .debounceTime(300)
      .distinctUntilChanged()
      .map(idSearch => ApplicationSearchQuery.forApplicationId(idSearch))
      .switchMap(search => this.applicationHub.searchApplications(search));
  }

  checkAll() {
    let selection = !this.allSelected;
    this.applicationRows.forEach(row => row.selected = selection);
    this.updateAllSelected();
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
    this.applicationRows.push(new ContentRow(application));
    this.updateApplications();
  }

  remove() {
    this.applicationRows = this.applicationRows.filter(row => !row.selected);
    this.updateApplications();
  }

  onIdentifierSearchChange(identifier: string) {
    this.applicationSearch.next(identifier);
  }

  sortBy(sort: Sort) {
    this.sort = sort;
    this.getProjectApplications().subscribe(rows => this.applicationRows = rows);
  }

  private getProjectApplications(): Observable<Array<ContentRow<Application>>> {
    let query = new ApplicationSearchQuery();
    query.projectId = this.project.id;
    query.sort = this.sort;

    return this.applicationHub.searchApplications(query)
      .map(applications => applications.map(app => new ContentRow(app)));
  }

  private updateApplications(): void {
    this.projectHub.updateProjectApplications(this.project.id, this.applicationRows.map(app => app.id))
      .subscribe(p => this.project = p);
  }

  private updateAllSelected(): void {
    this.allSelected = this.applicationRows.every(row => row.selected);
  }
}
