import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs/index';
import {Project} from '../../../model/project/project';
import * as fromProject from '../reducers';
import {Store} from '@ngrx/store';
import {RemoveParent} from '../actions/project-actions';
import {map, withLatestFrom} from 'rxjs/internal/operators';
import {Add} from '../actions/child-project-actions';
import {Search} from '../actions/project-search-actions';
import {ProjectSearchQuery} from '@model/project/project-search-query';

@Component({
  selector: 'related-projects',
  templateUrl: './related-projects.component.html',
  styleUrls: []
})
export class RelatedProjectsComponent implements OnInit {
  childProjects$: Observable<Project[]>;
  childProjectsLoading$: Observable<boolean>;
  parentProjects$: Observable<Project[]>;
  parentProjectsLoading$: Observable<boolean>;
  matchingProjects$: Observable<Project[]>;

  constructor(private store: Store<fromProject.State>) {}

  ngOnInit(): void {
    this.childProjects$ = this.store.select(fromProject.getChildProjects);
    this.childProjectsLoading$ = this.store.select(fromProject.getChildProjectsLoading);
    this.parentProjects$ = this.store.select(fromProject.getParentProjects);
    this.parentProjectsLoading$ = this.store.select(fromProject.getParentProjectsLoading);
  }

  projectSelectSearchChange(term: string): void {
    this.store.dispatch(new Search({identifier: term}));
    this.matchingProjects$ = this.store.select(fromProject.getMatchingList).pipe(
      withLatestFrom(this.store.select(fromProject.getCurrentProject)),
      // Filter those which already belong to this project
      map(([projects, current]) => this.filterEligibleProjects(current, projects)),
    );
  }

  addChild(id: number): void {
    this.store.dispatch(new Add(id));
  }

  removeChild(id: number): void {
    // remove childs parent
    this.store.dispatch(new RemoveParent([id]));
  }

  private filterEligibleProjects(parent: Project, matching: Project[]): Project[] {
    return matching.filter(p => {
      const notSelf = p.id !== parent.id;
      const notAlreadyIncluded = p.parentId !== parent.id;
      const notParentOfParent = p.id !== parent.parentId;
      return notSelf && notAlreadyIncluded && notParentOfParent;
    });
  }
}
