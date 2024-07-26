import {Component, OnInit, ViewChild} from '@angular/core';
import {Observable} from 'rxjs';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {MatLegacyPaginator as MatPaginator} from '@angular/material/legacy-paginator';
import {MatSort} from '@angular/material/sort';
import {Project} from '@model/project/project';
import {fromForm} from '@model/project/project-search-query';
import {CityDistrict} from '@model/common/city-district';
import {ProjectState} from '@service/project/project-state';
import {ProjectService} from '@service/project/project.service';
import {ProjectSearchDatasource} from '@service/project/project-search-datasource';
import {NotificationService} from '@feature/notification/notification.service';
import * as fromRoot from '@feature/allu/reducers';
import {select, Store} from '@ngrx/store';
import * as fromProject from '@feature/project/reducers';
import {map, take} from 'rxjs/operators';
import {SetSearchQuery} from '@feature/project/actions/project-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

@Component({
  selector: 'project-search',
  templateUrl: './project-search.component.html'
})
export class ProjectSearchComponent implements OnInit {

  displayedColumns = [
      'identifier', 'ownerName', 'active',
      'cityDistricts', 'startTime', 'endTime'
  ];

  projects: Array<Project> = [];
  queryForm: UntypedFormGroup;
  districts: Observable<Array<CityDistrict>>;
  dataSource: ProjectSearchDatasource;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  constructor(private projectService: ProjectService,
              private projectState: ProjectState,
              private store: Store<fromRoot.State>,
              private notification: NotificationService,
              fb: UntypedFormBuilder) {
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
    this.dataSource = new ProjectSearchDatasource(this.store, this.paginator, this.sort);
    this.districts = this.store.select(fromRoot.getAllCityDistricts);

    this.store.pipe(
      select(fromProject.getParameters),
      take(1),
      map(parameters => parameters || {})
    ).subscribe(parameters => this.queryForm.patchValue(parameters));
  }

  search(): void {
    const query = fromForm(this.queryForm.value);
    this.store.dispatch(new SetSearchQuery(ActionTargetType.Project, query));
  }

  districtNames(ids: Array<number>): Observable<Array<string>> {
    return this.projectState.districtNames(ids);
  }

  trackById(index: number, item: Project) {
    return item.id;
  }
}
