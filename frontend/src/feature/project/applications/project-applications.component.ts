import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';

import {ProjectHub} from '../../../service/project/project-hub';
import {Application} from '../../../model/application/application';
import {Project} from '../../../model/project/project';
import {ApplicationSearchQuery} from '../../../model/search/ApplicationSearchQuery';
import {ApplicationHub} from '../../../service/application/application-hub';


@Component({
  selector: 'project-applications',
  template: require('./project-applications.component.html'),
  styles: []
})
export class ProjectApplicationsComponent implements OnInit {

  project: Project;
  applications: Array<Application> = [];
  applicationSearch = new Subject<string>();
  matchingApplications: Observable<Array<Application>>;

  constructor(private route: ActivatedRoute, private projectHub: ProjectHub, private applicationHub: ApplicationHub) {}

  ngOnInit(): void {
    this.route.data
      .map((data: {project: Project}) => data.project)
      .subscribe(project => {
        this.project = project;

        this.projectHub.getProjectApplications(project.id)
          .map(apps => apps || [])
          .subscribe(apps => this.applications = apps);
      });

    this.matchingApplications = this.applicationSearch.asObservable()
      .debounceTime(300)
      .distinctUntilChanged()
      .map(idSearch => ApplicationSearchQuery.forApplicationId(idSearch))
      .switchMap(search => this.applicationHub.searchApplications(search));
  }

  add(application: Application) {
    this.applications.push(application);
    this.updateApplications();
  }

  remove(applicationId: number) {
    this.applications = this.applications.filter(app => applicationId !== app.id);
    this.updateApplications();
  }

  onIdentifierSearchChange(identifier: string) {
    this.applicationSearch.next(identifier);
  }

  private updateApplications(): void {
    this.projectHub.updateProjectApplications(this.project.id, this.applications.map(app => app.id))
      .subscribe(p => this.project = p);
  }
}
