import {Component, OnInit} from '@angular/core';
import {Project} from '../../model/project/project';
import {SidebarItem} from '../sidebar/sidebar-item';
import {ProjectState} from '../../service/project/project-state';
import {Observable} from 'rxjs';
import * as fromProject from './reducers';
import {Store} from '@ngrx/store';
import {Application} from '../../model/application/application';
import {map} from 'rxjs/internal/operators';

@Component({
  selector: 'project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.scss']
})
export class ProjectComponent implements OnInit {
  project$: Observable<Project>;
  applications$: Observable<Application[]>;
  childProjectCount$: Observable<number>;
  parent$: Observable<Project>;
  sidebarItems: Array<SidebarItem> = [];

  constructor(private projectState: ProjectState, private store: Store<fromProject.State>) {}

  ngOnInit(): void {
    this.project$ = this.store.select(fromProject.getCurrentProject);
    this.applications$ = this.store.select(fromProject.getApplications);
    this.childProjectCount$ = this.store.select(fromProject.getChildProjects).pipe(map(projects => projects.length));
    this.parent$ = this.store.select(fromProject.getParentProject);
    this.sidebarItems = this.sidebar();
  }

  sidebar(): Array<SidebarItem> {
    return [
      {type: 'BASIC_INFO'},
      {type: 'PROJECTS', count: this.childProjectCount$},
      {type: 'COMMENTS', count: this.store.select(fromProject.getCommentCount)},
      {type: 'HISTORY'}
    ];
  }
}
