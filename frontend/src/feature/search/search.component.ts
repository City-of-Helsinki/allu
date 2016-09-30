import {Component, OnInit, OnDestroy} from '@angular/core';
import {Router} from '@angular/router';

import {EventListener} from '../../event/event-listener';
import {EventService} from '../../event/event.service';
import {Event} from '../../event/event';

import {Application} from '../../model/application/application';
import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {ApplicationSearchEvent} from '../../event/search/application-search-event';
import {translations} from '../../util/translations';
import {ApplicationStatus} from '../../model/application/application-status-change';
import {PICKADATE_PARAMETERS, UI_DATE_FORMAT} from '../../util/time.util';
import {EnumUtil} from '../../util/enum.util';
import {ApplicationType} from '../../model/application/type/application-type';

@Component({
  selector: 'search',
  template: require('./search.component.html'),
  styles: [
    require('./search.component.scss')
  ]
})

export class SearchComponent implements EventListener, OnInit, OnDestroy {
  private results: Array<Application> = [];
  private items: Array<string> = ['Ensimmäinen', 'Toinen', 'Kolmas', 'Neljäs', 'Viides'];
  // TODO: handlers should be fetched from some service later
  private handlers: Array<string> = [
    'TestHandler'];
  private query: ApplicationSearchQuery = new ApplicationSearchQuery();
  private translations = translations;
  private pickadateParams = PICKADATE_PARAMETERS;
  private format = UI_DATE_FORMAT;
  private applicationStatusStrings = EnumUtil.enumValues(ApplicationStatus);
  private applicationTypeStrings = EnumUtil.enumValues(ApplicationType);

  constructor(private eventService: EventService, private router: Router) {
    this.results = [];

  }

  ngOnInit() {
    this.eventService.subscribe(this);
  }

  ngOnDestroy() {
    this.eventService.unsubscribe(this);
  }

  public goToSummary(application: Application): void {
    this.router.navigate(['/summary', application.id]);
  }

  public handle(event: Event): void {
    if (event instanceof ApplicationsAnnounceEvent) {
      console.log('SearchComponent apps', event);
      let aaEvent = <ApplicationsAnnounceEvent>event;
      this.results = aaEvent.applications.slice();
    }
  }

  private search(): void {
    console.log('Search clicked', this.query);
    this.eventService.send(this, new ApplicationSearchEvent(this.query));
  }
}
