import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Store} from '@ngrx/store';

import {Project} from '../../../model/project/project';
import {ProjectState} from '../../../service/project/project-state';
import * as fromProject from '../reducers';

@Component({
  selector: 'project-summary',
  templateUrl: './project-summary.component.html',
  styleUrls: ['./project-summary.component.scss']
})
export class ProjectSummaryComponent implements OnInit {
  project$: Observable<Project>;
  districts$: Observable<Array<string>>;

  constructor(private projectState: ProjectState, private store: Store<fromProject.State>) {}

  ngOnInit(): void {
    this.project$ = this.store.select(fromProject.getCurrentProject).take(1);
    this.districts$ = this.projectState.districtNames();
  }
}
