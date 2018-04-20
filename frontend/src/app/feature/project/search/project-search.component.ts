import {Component, OnInit, ViewChild} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {FormBuilder, FormGroup} from '@angular/forms';
import {MatPaginator, MatSort} from '@angular/material';

import {Project} from '../../../model/project/project';
import {ProjectSearchQuery} from '../../../model/project/project-search-query';
import {CityDistrict} from '../../../model/common/city-district';
import {ProjectState} from '../../../service/project/project-state';
import {CityDistrictService} from '../../../service/map/city-district.service';
import {ProjectService} from '../../../service/project/project.service';
import {ProjectSearchDatasource} from '../../../service/project/project-search-datasource';

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
              private cityDistrictService: CityDistrictService,
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
    this.dataSource = new ProjectSearchDatasource(this.projectService, this.paginator, this.sort);
    this.districts = this.cityDistrictService.get();
  }

  search(): void {
    const query = ProjectSearchQuery.fromForm(this.queryForm.value);
    console.log(query);
    this.dataSource.searchChange(query);
  }

  districtNames(ids: Array<number>): Observable<Array<string>> {
    return this.projectState.districtNames(ids);
  }

  trackById(index: number, item: Project) {
    return item.id;
  }
}
