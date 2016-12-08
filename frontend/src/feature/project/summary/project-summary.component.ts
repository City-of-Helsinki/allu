import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {Project} from '../../../model/project/project';
import {ProjectHub} from '../../../service/project/project-hub';
import {Some} from '../../../util/option';
import {Application} from '../../../model/application/application';
import {UI_DATE_FORMAT, TimeUtil} from '../../../util/time.util';
import {ApplicationStatus} from '../../../model/application/application-status';
import {translations} from '../../../util/translations';

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

  dateFormat = UI_DATE_FORMAT;
  translations = translations;

  constructor(private router: Router, private route: ActivatedRoute, private projectHub: ProjectHub) {}

  ngOnInit(): void {
    this.route.params.subscribe((params: {id: number}) => {
      Some(params.id).do(id => {
        this.projectHub.getProject(id).subscribe(project => {
          this.project = project;
          this.fetchApplications(id);
          this.fetchRelatedProjects(id);
          this.isActive = TimeUtil.isBetweenInclusive(new Date(), this.project.startTime, this.project.endTime);
        });
      });
    });
  }

  private fetchApplications(id: number): void {
    this.projectHub.getProjectApplications(id).subscribe(applications => {
      this.activeApplications = applications.filter(app => ApplicationStatus[app.status] < ApplicationStatus.DECISION);
      this.decidedApplications = applications.filter(app => ApplicationStatus[app.status] === ApplicationStatus.DECISION);
    });
  }

  private fetchRelatedProjects(id: number): void {
    this.projectHub.getParentProjects(id).subscribe(projects => this.parentProjects = projects);
    this.projectHub.getChildProjects(id).subscribe(projects => this.childProjects = projects);
  }
}
