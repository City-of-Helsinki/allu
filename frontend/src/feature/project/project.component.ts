import {Component, OnInit} from '@angular/core';
import {Project} from '../../model/project/project';
import {SidebarItem} from '../sidebar/sidebar-item';
import {Observable} from 'rxjs';
import {ProjectState} from '../../service/project/project-state';

@Component({
  selector: 'project',
  template: require('./project.component.html'),
  styles: []
})
export class ProjectComponent implements OnInit {
  project: Project;
  sidebarItems: Array<SidebarItem> = [];

  constructor(private projectState: ProjectState) {}

  ngOnInit(): void {
    this.project = this.projectState.project;
    this.sidebar().subscribe(items => {
      this.sidebarItems = items;
    });
  }

  sidebar(): Observable<Array<SidebarItem>> {
    return Observable.combineLatest(
      this.applicationCount(),
      this.projectCount(),
      (apps, projects) => [
        { type: 'BASIC_INFO'},
        { type: 'APPLICATIONS', count: apps},
        { type: 'PROJECTS', count: projects }
      ]
    );
  }

  projectCount(): Observable<number> {
    return Observable.combineLatest(
      this.projectState.childProjects,
      this.projectState.parentProjects,
      (childs, parents) => childs.length + parents.length);
  }

  applicationCount(): Observable<number> {
    return this.projectState.applications
      .map(applications => applications.length);
  }
}
