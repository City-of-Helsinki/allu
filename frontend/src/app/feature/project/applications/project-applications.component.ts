import {Component, Input, OnInit} from '@angular/core';
import * as fromProject from '../reducers';
import * as application from '../actions/application-actions';
import * as basket from '../actions/application-basket-actions';
import {Store} from '@ngrx/store';
import {ProjectState} from '../../../service/project/project-state';
import {Application} from '../../../model/application/application';
import {Observable} from 'rxjs';
import {Search} from '../actions/application-search-actions';
import {Some} from '../../../util/option';
import {map, take} from 'rxjs/internal/operators';

@Component({
  selector: 'project-applications',
  templateUrl: './project-applications.component.html',
  styleUrls: []
})
export class ProjectApplicationsComponent implements OnInit {
  @Input() projectId: number;

  applications: Observable<Application[]>;
  applicationsLoading: Observable<boolean>;
  applicationsInBasket: Observable<number>;
  matchingApplications: Observable<Application[]>;

  constructor(private projectState: ProjectState, private store: Store<fromProject.State>) {}

  ngOnInit(): void {
    this.store.dispatch(new application.Load());
    this.applications = this.store.select(fromProject.getApplications);
    this.applicationsLoading = this.store.select(fromProject.getApplicationsLoading);
    this.applicationsInBasket = this.store.select(fromProject.getApplicationCountInBasket);
  }

  applicationSelectSearchChange(term: string): void {
    this.store.dispatch(new Search(term));
    this.matchingApplications = this.store.select(fromProject.getMatchingApplications).pipe(
      map(applications => this.filterAlreadyIncludedApplications(applications))
    );
  }

  applicationSelected(id: number): void {
    this.store.dispatch(new application.Add(id));
  }

  removeApplication(id: number): void {
    this.store.dispatch(new application.Remove(id));
  }

  addFromBasket(): void {
    this.store.select(fromProject.getApplicationIdsInBasket).pipe(take(1))
      .subscribe((ids: number[]) => {
        this.store.dispatch(new application.AddMultiple(ids));
        this.store.dispatch(new basket.Clear());
      });
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
