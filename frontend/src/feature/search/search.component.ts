import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {Application} from '../../model/application/application';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {translations} from '../../util/translations';
import {ApplicationStatus} from '../../model/application/application-status';
import {PICKADATE_PARAMETERS, UI_DATE_FORMAT} from '../../util/time.util';
import {EnumUtil} from '../../util/enum.util';
import {ApplicationType} from '../../model/application/type/application-type';
import {ApplicationHub} from '../../service/application/application-hub';
import {UserHub} from '../../service/user/user-hub';
import {User} from '../../model/common/user';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Sort} from '../../model/common/sort';
import {ApplicationState} from '../../service/application/application-state';
import {MapHub} from '../../service/map/map-hub';
import {CityDistrict} from '../../model/common/city-district';
import {Some} from '../../util/option';

@Component({
  selector: 'search',
  template: require('./search.component.html')
})
export class SearchComponent implements OnInit {

  sort: Sort = new Sort(undefined, undefined);
  queryForm: FormGroup;
  applications: Array<Application>;
  handlers: Observable<Array<User>>;
  districts: Observable<Array<CityDistrict>>;
  private items: Array<string> = ['Ensimmäinen', 'Toinen', 'Kolmas', 'Neljäs', 'Viides'];
  private translations = translations;
  private pickadateParams = PICKADATE_PARAMETERS;
  private format = UI_DATE_FORMAT;
  private applicationStatusStrings = EnumUtil.enumValues(ApplicationStatus);
  private applicationTypeStrings = EnumUtil.enumValues(ApplicationType);
  private selections = [];

  constructor(private applicationHub: ApplicationHub,
              private applicationState: ApplicationState,
              private userHub: UserHub,
              private mapHub: MapHub,
              private router: Router,
              private fb: FormBuilder) {
    this.queryForm = fb.group({
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

  newApplication(): void {
    this.applicationState.application = new Application();
    this.router.navigate(['applications/location']);
  }

  sortBy(sort: Sort) {
    this.sort = sort;
    this.search();
  }

  search(): void {
    this.applicationHub.searchApplications(ApplicationSearchQuery.from(this.queryForm.value, this.sort)).subscribe(apps => {
      this.applications = apps;
    });
  }

  districtName(id: number): Observable<string> {
    return id !== undefined ? this.mapHub.districtById(id).map(d => d.name) : Observable.empty();
  }
}
