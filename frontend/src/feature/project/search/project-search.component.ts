import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {FormBuilder, FormGroup} from '@angular/forms';

import {Project} from '../../../model/project/project';
import {translations} from '../../../util/translations';
import {PICKADATE_PARAMETERS} from '../../../util/time.util';
import {UI_PIPE_DATE_FORMAT} from '../../../util/time.util';
import {ProjectSearchQuery} from '../../../model/project/project-search-query';
import {ProjectHub} from '../../../service/project/project-hub';
import {Sort} from '../../../model/common/sort';
import {CityDistrict} from '../../../model/common/city-district';
import {ProjectState} from '../../../service/project/project-state';
import {MapHub} from '../../../service/map/map-hub';


@Component({
  selector: 'project-search',
  template: require('./project-search.component.html')
})
export class ProjectSearchComponent implements OnInit {
  sort: Sort = new Sort(undefined, undefined);
  projects: Array<Project> = [];
  queryForm: FormGroup;
  districts: Observable<Array<CityDistrict>>;
  private translations = translations;
  private pickadateParams = PICKADATE_PARAMETERS;
  private format = UI_PIPE_DATE_FORMAT;
  private selections = [];

  constructor(private projectHub: ProjectHub,
              private projectState: ProjectState,
              private mapHub: MapHub,
              private router: Router,
              private fb: FormBuilder) {
    this.queryForm = fb.group({
      id: undefined,
      startTime: undefined,
      endTime: undefined,
      ownerName: undefined,
      onlyActive: true,
      districts: undefined,
      creator: undefined
    });
  }

  ngOnInit(): void {
    this.districts = this.mapHub.districts();
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

  districtNames(ids: Array<number>): Observable<Array<string>> {
    return this.projectState.districtNames(ids);
  }
}
