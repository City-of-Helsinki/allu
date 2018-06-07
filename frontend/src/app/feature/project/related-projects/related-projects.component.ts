import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs/index';
import {Project} from '../../../model/project/project';
import * as fromProject from '../reducers';
import {Store} from '@ngrx/store';

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

  constructor(private store: Store<fromProject.State>) {}

  ngOnInit(): void {
    this.childProjects$ = this.store.select(fromProject.getChildProjects);
    this.childProjectsLoading$ = this.store.select(fromProject.getChildProjectsLoading);
    this.parentProjects$ = this.store.select(fromProject.getParentProjects);
    this.parentProjectsLoading$ = this.store.select(fromProject.getParentProjectsLoading);
  }
}
