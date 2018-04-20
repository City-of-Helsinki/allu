import {Component, OnInit} from '@angular/core';
import {Project} from '../../model/project/project';
import {SidebarItem, SidebarItemType} from '../sidebar/sidebar-item';
import {ProjectState} from '../../service/project/project-state';
import {Observable} from 'rxjs/Observable';
import * as fromProject from './reducers';
import {Store} from '@ngrx/store';
import {Application} from '../../model/application/application';
import * as parentProjects from './actions/parent-project-actions';
import * as childProjects from './actions/child-project-actions';
import * as application from './actions/application-actions';

@Component({
  selector: 'project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.scss']
})
export class ProjectComponent implements OnInit {
  project$: Observable<Project>;
  applications$: Observable<Application[]>;
  relatedProjectCount$: Observable<number>;
  sidebarItems: Array<SidebarItem> = [];

  constructor(private projectState: ProjectState, private store: Store<fromProject.State>) {}

  ngOnInit(): void {
    this.store.dispatch(new application.Load());
    this.store.dispatch(new parentProjects.Load());
    this.store.dispatch(new childProjects.Load());

    this.project$ = this.store.select(fromProject.getCurrentProject);
    this.applications$ = this.store.select(fromProject.getApplications);
    this.relatedProjectCount$ = this.store.select(fromProject.getRelatedProjects).map(projects => projects.length);
    this.sidebarItems = this.sidebar();
  }

  sidebar(): Array<SidebarItem> {
    return [
      this.sidebarItem('BASIC_INFO'),
      this.sidebarItem('PROJECTS', this.relatedProjects)
    ];
  }

  get relatedProjects(): Observable<number> {
    return Observable.combineLatest(
      this.projectState.childProjects,
      this.projectState.parentProjects,
      (childs, parents) => childs.length + parents.length);
  }

  private sidebarItem(type: SidebarItemType, count?: Observable<number>): SidebarItem {
    return {type: type, count: count};
  }
}
