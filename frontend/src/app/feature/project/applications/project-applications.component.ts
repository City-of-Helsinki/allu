import {Component, Input, OnInit} from '@angular/core';
import * as fromProject from '../reducers';
import * as application from '../actions/application-actions';
import {Store} from '@ngrx/store';
import {ProjectState} from '../../../service/project/project-state';
import {Application} from '../../../model/application/application';
import {Observable} from 'rxjs/Observable';
import {Search} from '../actions/application-search-actions';
import {Some} from '../../../util/option';

@Component({
  selector: 'project-applications',
  templateUrl: './project-applications.component.html',
  styleUrls: []
})
export class ProjectApplicationsComponent implements OnInit {
  @Input() projectId: number;

  applications: Observable<Application[]>;
  applicationsLoading: Observable<boolean>;
  matchingApplications: Observable<Application[]>;

  constructor(private projectState: ProjectState, private store: Store<fromProject.State>) {}

  ngOnInit(): void {
    this.applications = this.store.select(fromProject.getApplications);
    this.applicationsLoading = this.store.select(fromProject.getApplicationsLoading);
  }

  applicationSelectSearchChange(term: string): void {
    this.store.dispatch(new Search(term));
    this.matchingApplications = this.store.select(fromProject.getMatchingApplications)
      .map(applications => this.filterAlreadyIncludedApplications(applications));
  }

  applicationSelected(id: number): void {
    this.store.dispatch(new application.Add(id));
  }

  removeApplication(id: number): void {
    this.store.dispatch(new application.Remove(id));
  }

  private filterAlreadyIncludedApplications(applications: Application[]): Application[] {
    return applications.filter((app) => !this.includedInProject(app));
  }

  private includedInProject(app: Application): boolean {
    return Some(app.project)
      .map(p => p.id)
      .map(id => id === this.projectId)
      .orElse(false);
  }
}
