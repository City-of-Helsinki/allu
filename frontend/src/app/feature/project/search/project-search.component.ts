import {Component, OnInit, ViewChild} from '@angular/core';
import {Observable} from 'rxjs';
import {FormBuilder, FormGroup} from '@angular/forms';
import {MatPaginator, MatSort} from '@angular/material';

import {Project} from '../../../model/project/project';
import {ProjectSearchQuery} from '../../../model/project/project-search-query';
import {CityDistrict} from '../../../model/common/city-district';
import {ProjectState} from '../../../service/project/project-state';
import {ProjectService} from '../../../service/project/project.service';
import {ProjectSearchDatasource} from '../../../service/project/project-search-datasource';
import {NotificationService} from '../../notification/notification.service';
import * as fromRoot from '../../allu/reducers';
import {Store} from '@ngrx/store';

@Component({
  selector: 'project-search',
  templateUrl: './project-search.component.html'
})
export class ProjectSearchComponent implements OnInit {

  displayedColumns = [
      'id', 'ownerName', 'active',
      'cityDistricts', 'startTime', 'endTime'
  ];

  projects: Array<Project> = [];
  queryForm: FormGroup;
  districts: Observable<Array<CityDistrict>>;
  dataSource: ProjectSearchDatasource;

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private projectService: ProjectService,
              private projectState: ProjectState,
              private store: Store<fromRoot.State>,
              private notification: NotificationService,
              fb: FormBuilder) {
    this.queryForm = fb.group({
      identifier: undefined,
      startTime: undefined,
      endTime: undefined,
      ownerName: undefined,
      onlyActive: true,
      districts: undefined,
      creator: undefined
    });
  }

  ngOnInit(): void {
    this.dataSource = new ProjectSearchDatasource(this.projectService, this.notification, this.paginator, this.sort);
    this.districts = this.store.select(fromRoot.getAllCityDistricts);
  }

  search(): void {
    const query = ProjectSearchQuery.fromForm(this.queryForm.value);
    this.dataSource.searchChange(query);
  }

  districtNames(ids: Array<number>): Observable<Array<string>> {
    return this.projectState.districtNames(ids);
  }

  trackById(index: number, item: Project) {
    return item.id;
  }
}
