import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {Project} from '../../../model/project/project';
import {Application} from '../../../model/application/application';
import {TimeUtil, UI_PIPE_DATE_FORMAT} from '../../../util/time.util';
import {ApplicationStatus} from '../../../model/application/application-status';
import {translations} from '../../../util/translations';
import {ProjectState} from '../../../service/project/project-state';

@Component({
  selector: 'project-summary',
  template: require('./project-summary.component.html'),
  styles: [require('./project-summary.component.scss')]
})
export class ProjectSummaryComponent implements OnInit {
  project: Project = new Project();
  activeApplications: Array<Application> = [];
  decidedApplications: Array<Application> = [];
  parentProjects: Array<Project> = [];
  childProjects: Array<Project> = [];
  history: Observable<Array<string>>; // TODO: history
  isActive: boolean;
  districts: Observable<Array<string>>;
  dateFormat = UI_PIPE_DATE_FORMAT;
  translations = translations;

  constructor(private projectState: ProjectState) {}

  ngOnInit(): void {
    this.project = this.projectState.project;
    this.districts = this.projectState.districtNames();
    this.isActive = TimeUtil.isBetweenInclusive(new Date(), this.project.startTime, this.project.endTime);
    this.fetchApplications();
    this.fetchRelatedProjects();
  }

  private fetchApplications(): void {
    this.projectState.applications.subscribe(applications => {
      this.activeApplications = applications.filter(app => ApplicationStatus[app.status] < ApplicationStatus.DECISION);
      this.decidedApplications = applications.filter(app => ApplicationStatus[app.status] === ApplicationStatus.DECISION);
    });
  }

  private fetchRelatedProjects(): void {
    this.projectState.parentProjects.subscribe(projects => this.parentProjects = projects);
    this.projectState.childProjects.subscribe(projects => this.childProjects = projects);
  }
}
