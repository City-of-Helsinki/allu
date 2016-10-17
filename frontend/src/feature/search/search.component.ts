import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {Application} from '../../model/application/application';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {translations} from '../../util/translations';
import {ApplicationStatus} from '../../model/application/application-status-change';
import {PICKADATE_PARAMETERS, UI_DATE_FORMAT} from '../../util/time.util';
import {EnumUtil} from '../../util/enum.util';
import {ApplicationType} from '../../model/application/type/application-type';
import {ApplicationHub} from '../../service/application/application-hub';
import {UserHub} from '../../service/user/user-hub';
import {User} from '../../model/common/user';
import {FormBuilder, FormGroup} from '@angular/forms';

@Component({
  selector: 'search',
  template: require('./search.component.html'),
  styles: [
    require('./search.component.scss')
  ]
})
export class SearchComponent implements OnInit {
  private queryForm: FormGroup;
  private applications: Observable<Array<Application>>;
  private items: Array<string> = ['Ensimmäinen', 'Toinen', 'Kolmas', 'Neljäs', 'Viides'];
  // TODO: handlers should be fetched from some service later
  private handlers: Observable<Array<User>>;
  private translations = translations;
  private pickadateParams = PICKADATE_PARAMETERS;
  private format = UI_DATE_FORMAT;
  private applicationStatusStrings = EnumUtil.enumValues(ApplicationStatus);
  private applicationTypeStrings = EnumUtil.enumValues(ApplicationType);

  constructor(private applicationHub: ApplicationHub, private userHub: UserHub, private router: Router, fb: FormBuilder) {
    this.queryForm = fb.group({
      applicationId: undefined,
      type: undefined,
      status: undefined,
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
  }

  public goToSummary(application: Application): void {
    this.router.navigate(['applications', application.id, 'summary']);
  }

  private search(): void {
    console.log('search query', ApplicationSearchQuery.from(this.queryForm.value));
    this.applications = this.applicationHub.searchApplications(ApplicationSearchQuery.from(this.queryForm.value));
  }
}
