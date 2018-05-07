import {Component, OnInit, ViewChild} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {Application} from '../../model/application/application';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {ApplicationStatus, searchable} from '../../model/application/application-status';
import {EnumUtil} from '../../util/enum.util';
import {ApplicationType} from '../../model/application/type/application-type';
import {UserHub} from '../../service/user/user-hub';
import {User} from '../../model/user/user';
import {FormBuilder, FormGroup} from '@angular/forms';
import {CityDistrict} from '../../model/common/city-district';
import {ApplicationService} from '../../service/application/application.service';
import {MatPaginator, MatSort} from '@angular/material';
import {ApplicationSearchDatasource} from '../../service/application/application-search-datasource';
import {NotificationService} from '../../service/notification/notification.service';
import * as fromRoot from '../allu/reducers';
import {Store} from '@ngrx/store';

@Component({
  selector: 'search',
  templateUrl: './search.component.html'
})
export class SearchComponent implements OnInit {

  displayedColumns = [
    'owner.realName', 'applicationId', 'name', 'type', 'status', 'project.name',
    'customers.applicant.customer.name', 'locations.streetAddress', 'locations.cityDistrictId',
    'creationTime', 'startTime'
  ];

  queryForm: FormGroup;
  applications: Array<Application>;
  owners: Observable<Array<User>>;
  districts: Observable<Array<CityDistrict>>;
  applicationStatusStrings = searchable.map(status => ApplicationStatus[status]);
  applicationTypeStrings = EnumUtil.enumValues(ApplicationType);
  dataSource: ApplicationSearchDatasource;

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private applicationService: ApplicationService,
              private userHub: UserHub,
              private store: Store<fromRoot.State>,
              private fb: FormBuilder,
              private notification: NotificationService) {
    this.queryForm = this.fb.group({
      applicationId: undefined,
      type: undefined,
      status: undefined,
      districts: undefined,
      owner: undefined,
      address: undefined,
      applicant: undefined,
      contact: undefined,
      freeText: undefined,
      startTime: undefined,
      endTime: undefined
    });
  }

  ngOnInit(): void {
    this.dataSource = new ApplicationSearchDatasource(this.applicationService, this.notification, this.paginator, this.sort);
    this.owners = this.userHub.getActiveUsers();
    this.districts = this.store.select(fromRoot.getAllCityDistricts);
  }

  search(): void {
    const query = ApplicationSearchQuery.from(this.queryForm.value);
    this.dataSource.searchChange(query);
  }

  districtName(id: number): Observable<string> {
    return this.store.select(fromRoot.getCityDistrictName(id));
  }

  trackById(index: number, item: Application) {
    return item.id;
  }
}
