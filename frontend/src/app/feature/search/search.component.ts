import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {Application} from '../../model/application/application';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {ApplicationStatus} from '../../model/application/application-status';
import {EnumUtil} from '../../util/enum.util';
import {ApplicationType} from '../../model/application/type/application-type';
import {UserHub} from '../../service/user/user-hub';
import {User} from '../../model/user/user';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Sort} from '../../model/common/sort';
import {MapHub} from '../../service/map/map-hub';
import {CityDistrict} from '../../model/common/city-district';
import {NotificationService} from '../../service/notification/notification.service';
import {ApplicationService} from '../../service/application/application.service';

@Component({
  selector: 'search',
  templateUrl: './search.component.html'
})
export class SearchComponent implements OnInit {

  sort: Sort = new Sort(undefined, undefined);
  queryForm: FormGroup;
  applications: Array<Application>;
  handlers: Observable<Array<User>>;
  districts: Observable<Array<CityDistrict>>;
  applicationStatusStrings = EnumUtil.enumValues(ApplicationStatus);
  applicationTypeStrings = EnumUtil.enumValues(ApplicationType);

  constructor(private applicationService: ApplicationService,
              private userHub: UserHub,
              private mapHub: MapHub,
              private router: Router,
              private fb: FormBuilder) {
    this.queryForm = this.fb.group({
      applicationId: undefined,
      type: undefined,
      status: undefined,
      districts: undefined,
      handler: undefined,
      address: undefined,
      applicant: undefined,
      contact: undefined,
      freeText: undefined,
      startTime: undefined,
      endTime: undefined
    });
  }

  ngOnInit(): void {
    this.handlers = this.userHub.getActiveUsers();
    this.districts = this.mapHub.districts();
  }

  goToSummary(application: Application): void {
    this.router.navigate(['applications', application.id, 'summary']);
  }

  sortBy(sort: Sort) {
    this.sort = sort;
    this.search();
  }

  search(): void {
    const query = ApplicationSearchQuery.from(this.queryForm.value, this.sort);
    this.applicationService.search(query)
      .subscribe(
        apps => this.applications = apps,
        err => {
          NotificationService.error(err);
          this.applications = [];
        }
    );
  }

  districtName(id: number): Observable<string> {
    return id !== undefined ? this.mapHub.districtById(id).map(d => d.name) : Observable.empty();
  }
}
