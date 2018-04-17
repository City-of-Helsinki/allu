import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Store} from '@ngrx/store';

import {Project} from '../../../model/project/project';
import {TimeUtil} from '../../../util/time.util';
import {ProjectState} from '../../../service/project/project-state';
import * as fromProject from '../reducers';
import * as project from '../actions/project-actions';

@Component({
  selector: 'project-summary',
  templateUrl: './project-summary.component.html',
  styleUrls: ['./project-summary.component.scss']
})
export class ProjectSummaryComponent implements OnInit {
  project: Project = new Project();
  parentProjects: Array<Project> = [];
  childProjects: Array<Project> = [];
  history: Observable<Array<string>>; // TODO: history
  isActive: boolean;
  districts: Observable<Array<string>>;

  constructor(private projectState: ProjectState, private store: Store<fromProject.State>) {}

  ngOnInit(): void {
    this.project = this.projectState.project;
    this.store.dispatch(new project.LoadSuccess(this.project));

    this.districts = this.projectState.districtNames();
    this.isActive = TimeUtil.isBetweenInclusive(new Date(), this.project.startTime, this.project.endTime);
    this.fetchRelatedProjects();
  }

  private fetchRelatedProjects(): void {
    this.projectState.parentProjects.subscribe(projects => this.parentProjects = projects);
    this.projectState.childProjects.subscribe(projects => this.childProjects = projects);
  }
}
