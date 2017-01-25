import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';

import {ProjectHub} from '../../../service/project/project-hub';
import {Project} from '../../../model/project/project';
import {Some} from '../../../util/option';
import {UI_DATE_FORMAT} from '../../../util/time.util';
import {ContentRow} from '../../../model/common/content-row';
import {ProjectSearchQuery} from '../../../model/project/project-search-query';
import {Sort} from '../../../model/common/sort';
import {ProjectState} from '../../../service/project/project-state';



@Component({
  selector: 'project-projects',
  template: require('./project-projects.component.html'),
  styles: []
})
export class ProjectProjectsComponent implements OnInit {

  project: Project;
  sortedProjectRows: Array<ContentRow<Project>> = [];
  projectSearch = new Subject<number>();
  matchingProjects: Observable<Array<Project>>;
  allSelected = false;
  sort: Sort = new Sort(undefined, undefined);
  dateFormat = UI_DATE_FORMAT;

  private projectRows: Array<ContentRow<Project>> = [];

  constructor(private router: Router,
              private route: ActivatedRoute,
              private projectHub: ProjectHub,
              private projectState: ProjectState) {}

  ngOnInit(): void {
    this.route.data
      .map((data: {project: Project}) => data.project)
      .subscribe(project => {
        this.project = project;

        Some(project.id).do(id => this.fetchRelatedProjects());
      });

    this.matchingProjects = this.projectSearch.asObservable()
      .debounceTime(300)
      .distinctUntilChanged()
      .map(id => ProjectSearchQuery.fromProjectId(id))
      .switchMap(search => this.projectHub.searchProjects(search));
  }

  add(project: Project) {
    if (this.notAdded(project.id)) {
      let row = new ContentRow(project);
      this.projectRows.push(row);
      this.projectState.updateParentProject(project)
        .subscribe(updated => this.fetchRelatedProjects());
    }
  }

  remove() {
    let removeParentsFrom = this.projectRows
      .filter(row => row.selected)
      .map(row => row.id);

    this.projectState.removeParentsFrom(removeParentsFrom)
      .subscribe(updated => this.fetchRelatedProjects());
  }

  onIdentifierSearchChange(identifier: string) {
    Some(identifier)
      .map(id => Number(identifier))
      .filter(id => !Number.isNaN(id))
      .do(id => this.projectSearch.next(id));
  }

  isChild(parentId: number): boolean {
    return this.project.id === parentId;
  }


  checkAll() {
    let selection = !this.allSelected;
    this.projectRows.forEach(row => row.selected = selection);
    this.updateAllSelected();
  }

  checkSingle(row: ContentRow<Project>) {
    row.selected = !row.selected;
    this.updateAllSelected();
  }

  goToInfo(col: number, row: ContentRow<Project>): void {
    // undefined and 0 should not trigger navigation
    if (col) {
      this.router.navigate(['projects', row.id, 'info']);
    }
  }

  sortBy(sort: Sort): void {
    this.sort = sort;
    this.sortedProjectRows = this.sortRows(this.sort, this.projectRows);
  }

  districtNames(ids: Array<number>): Observable<Array<string>> {
    return this.projectState.districtNames(ids);
  }

  private updateAllSelected() {
    this.allSelected = this.projectRows.every(row => row.selected);
  }

  private fetchRelatedProjects(): void {
    this.projectState.relatedProjects.map(projects => projects.map(project => new ContentRow(project)))
      .subscribe(projects => {
        this.projectRows = projects;
        this.sortedProjectRows = this.sortRows(this.sort, projects);
      });
  }

  private notAdded(id: number): boolean {
    return this.projectRows.map(p => p.id).indexOf(id) < 0;
  }

  private sortRows(sort: Sort, rows: Array<ContentRow<Project>>): Array<ContentRow<Project>> {
    let original = rows;
    let sorted =  rows
      .map(row => row.content)
      .sort(sort.sortFn())
      .map(project => new ContentRow(project));

    return sort.byDirection(original, sorted);
  }
}

