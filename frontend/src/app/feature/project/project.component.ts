import {Component, OnInit} from '@angular/core';
import {Project} from '../../model/project/project';
import {SidebarItem, SidebarItemType} from '../sidebar/sidebar-item';
import {ProjectState} from '../../service/project/project-state';
import {Observable} from 'rxjs/Observable';

@Component({
  selector: 'project',
  templateUrl: './project.component.html',
  styleUrls: []
})
export class ProjectComponent implements OnInit {
  project: Project;
  sidebarItems: Array<SidebarItem> = [];

  constructor(private projectState: ProjectState) {}

  ngOnInit(): void {
    this.project = this.projectState.project;
    this.sidebarItems = this.sidebar();
  }

  sidebar(): Array<SidebarItem> {
    return [
      this.sidebarItem('BASIC_INFO'),
      this.sidebarItem('APPLICATIONS', this.applicationCount),
      this.sidebarItem('PROJECTS', this.projectCount)
    ];
  }

  private get projectCount(): Observable<number> {
    return Observable.combineLatest(
      this.projectState.childProjects,
      this.projectState.parentProjects,
      (childs, parents) => childs.length + parents.length);
  }

  private get applicationCount(): Observable<number> {
    return this.projectState.applications
      .map(applications => applications.length);
  }

  private sidebarItem(type: SidebarItemType, count?: Observable<number>): SidebarItem {
    return {type: type, count: count};
  }
}
