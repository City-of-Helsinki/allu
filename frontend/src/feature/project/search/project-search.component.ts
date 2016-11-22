import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {FormBuilder, FormGroup} from '@angular/forms';

import {Project} from '../../../model/project/project';
import {translations} from '../../../util/translations';
import {PICKADATE_PARAMETERS} from '../../../util/time.util';
import {UI_DATE_FORMAT} from '../../../util/time.util';
import {ProjectSearchQuery} from '../../../model/project/project-search-query';
import {ProjectHub} from '../../../service/project/project-hub';


@Component({
  selector: 'project-search',
  template: require('./project-search.component.html')
})
export class ProjectSearchComponent implements OnInit {
  private queryForm: FormGroup;
  private projects: Observable<Array<Project>>;
  private translations = translations;
  private pickadateParams = PICKADATE_PARAMETERS;
  private format = UI_DATE_FORMAT;
  private selections = [];

  constructor(private projectHub: ProjectHub, private router: Router, private fb: FormBuilder) {
    this.queryForm = fb.group({
      id: undefined,
      startTime: undefined,
      endTime: undefined,
      ownerName: undefined,
      status: undefined,
      district: undefined,
      creator: undefined
    });
  }

  ngOnInit(): void {
  }

  public goToSummary(project: Project): void {
    // TODO: go to actual summary when page implemented
    this.router.navigate(['projects', project.id]);
  }

  private search(): void {
    let query = ProjectSearchQuery.fromForm(this.queryForm.value);
    this.projects = this.projectHub.searchProjects(query);
  }
}
