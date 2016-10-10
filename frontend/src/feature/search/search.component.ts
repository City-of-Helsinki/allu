import {Component} from '@angular/core';
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

@Component({
  selector: 'search',
  template: require('./search.component.html'),
  styles: [
    require('./search.component.scss')
  ]
})
export class SearchComponent {
  private applications: Observable<Array<Application>>;
  private items: Array<string> = ['Ensimmäinen', 'Toinen', 'Kolmas', 'Neljäs', 'Viides'];
  // TODO: handlers should be fetched from some service later
  private handlers: Array<string> = ['TestHandler'];
  private query: ApplicationSearchQuery = new ApplicationSearchQuery();
  private translations = translations;
  private pickadateParams = PICKADATE_PARAMETERS;
  private format = UI_DATE_FORMAT;
  private applicationStatusStrings = EnumUtil.enumValues(ApplicationStatus);
  private applicationTypeStrings = EnumUtil.enumValues(ApplicationType);

  constructor(private applicationHub: ApplicationHub, private router: Router) {
  }

  public goToSummary(application: Application): void {
    this.router.navigate(['applications', application.id, 'summary']);
  }

  private search(): void {
    this.applications = this.applicationHub.searchApplications(this.query);
  }
}
