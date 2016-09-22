import {Component, OnInit, OnDestroy} from '@angular/core';
import {Router} from '@angular/router';
import { MdButton } from '@angular2-material/button';
import { MD_CARD_DIRECTIVES } from '@angular2-material/card';
import {MaterializeDirective} from 'angular2-materialize';

import {EventListener} from '../../event/event-listener';
import {EventService} from '../../event/event.service';
import {Event} from '../../event/event';

import {Application} from '../../model/application/application';
import {ApplicationsAnnounceEvent} from '../../event/announce/applications-announce-event';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {ApplicationSearchEvent} from '../../event/search/application-search-event';
import {translations} from '../../util/translations';
import {ApplicationStatus} from '../../model/application/application-status-change';
import {PICKADATE_PARAMETERS} from '../../util/time.util';

@Component({
  selector: 'search',
  moduleId: module.id,
  template: require('./search.component.html'),
  styles: [
    require('./search.component.scss')
  ],
  directives: [MD_CARD_DIRECTIVES, MaterializeDirective, MdButton]
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

  private getApplicationStatusStrings(): Array<string> {
    let statusStrings: Array<string> = [];
    for (let item in ApplicationStatus) {
      if (isNaN(parseInt(item, 10))) {
        statusStrings.push(item);
      }
    }
    return statusStrings;
  }
}
