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
import {Sort} from '../../../model/common/sort';


@Component({
  selector: 'project-search',
  template: require('./project-search.component.html')
})
export class ProjectSearchComponent implements OnInit {
  sort: Sort = new Sort(undefined, undefined);
  projects: Array<Project> = [];
  queryForm: FormGroup;
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
      onlyActive: true,
      district: undefined,
      creator: undefined
    });
  }

  ngOnInit(): void {
  }

  goToSummary(project: Project): void {
    this.router.navigate(['projects', project.id]);
  }

  sortBy(sort: Sort) {
    this.sort = sort;
    this.search();
  }

  search(): void {
    let query = ProjectSearchQuery.fromForm(this.queryForm.value, this.sort);
    this.projectHub.searchProjects(query).subscribe(projects => {
      this.projects = projects;
    });
  }
}
