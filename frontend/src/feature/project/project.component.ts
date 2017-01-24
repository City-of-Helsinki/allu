import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Project} from '../../model/project/project';
import {SidebarItem} from '../sidebar/sidebar-item';
import {ProjectHub} from '../../service/project/project-hub';
import {ApplicationHub} from '../../service/application/application-hub';
import {Observable} from 'rxjs';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';

@Component({
  selector: 'project',
  template: require('./project.component.html'),
  styles: []
})
export class ProjectComponent implements OnInit {
  project: Project;
  sidebarItems: Array<SidebarItem> = [];

  constructor(private route: ActivatedRoute,
              private projectHub: ProjectHub,
              private applicationHub: ApplicationHub) {}

  ngOnInit(): void {
    this.route.data
      .map((data: {project: Project}) => data.project)
      .subscribe(project => {
        this.project = project;

        this.sidebar().subscribe(items => {
          this.sidebarItems = items;
        });
      });
  }

  sidebar(): Observable<Array<SidebarItem>> {
    return Observable.combineLatest(
      this.applicationCount(this.project.id),
      this.projectCount(this.project.id),
      (apps, projects) => [
        { type: 'BASIC_INFO'},
        { type: 'APPLICATIONS', count: apps},
        { type: 'PROJECTS', count: projects }
      ]
    );

  }

  projectCount(projectId: number): Observable<number> {
    return Observable.combineLatest(
      this.projectHub.getChildProjects(projectId),
      this.projectHub.getParentProjects(projectId),
      (childs, parents) => childs.length + parents.length);
  }

  applicationCount(projectId: number): Observable<number> {
    let query = new ApplicationSearchQuery();
    query.projectId = projectId;

    return this.applicationHub.searchApplications(query)
      .map(applications => applications.length);
  }
}
