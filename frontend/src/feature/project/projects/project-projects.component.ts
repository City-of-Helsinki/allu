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



@Component({
  selector: 'project-projects',
  template: require('./project-projects.component.html'),
  styles: []
})
export class ProjectProjectsComponent implements OnInit {

  project: Project;
  relatedProjects: Array<ContentRow<Project>> = [];
  projectSearch = new Subject<number>();
  matchingProjects: Observable<Array<Project>>;
  allSelected = false;

  dateFormat = UI_DATE_FORMAT;

  constructor(private router: Router, private route: ActivatedRoute, private projectHub: ProjectHub) {}

  ngOnInit(): void {
    this.route.data
      .map((data: {project: Project}) => data.project)
      .subscribe(project => {
        this.project = project;

        Some(project.id).do(id => this.fetchRelatedProjects(id));
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
      this.relatedProjects.push(row);
      this.projectHub.updateParent(project.id, this.project.id)
        .subscribe(updated => this.fetchRelatedProjects(this.project.id));
    }
  }

  remove() {
    let removeParentsFrom = this.relatedProjects
      .filter(row => row.selected)
      .map(row => row.id);

    if (removeParentsFrom.length > 0) {
      this.projectHub.removeParent(removeParentsFrom)
        .subscribe(response => this.fetchRelatedProjects(this.project.id));
    }
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
    this.relatedProjects.forEach(row => row.selected = selection);
    this.updateAllSelected();
  }

  checkSingle(row: ContentRow<Project>) {
    row.selected = !row.selected;
    this.updateAllSelected();
  }

  goToSummary(col: number, row: ContentRow<Project>): void {
    // undefined and 0 should not trigger navigation
    if (col) {
      this.router.navigate(['projects', row.id, 'summary']);
    }
  }

  private updateAllSelected() {
    this.allSelected = this.relatedProjects.every(row => row.selected);
  }

  private fetchRelatedProjects(id: number): void {
    Observable.combineLatest(
      this.projectHub.getParentProjects(id),
      this.projectHub.getChildProjects(id)
    ).map(projects => [].concat.apply([], projects)) // maps array[array, array] => array
      .map(projects => projects.map(project => new ContentRow(project)))
      .subscribe(projects => this.relatedProjects = projects);
  }

  private notAdded(id: number): boolean {
    return this.relatedProjects.map(p => p.id).indexOf(id) < 0;
  }
}

