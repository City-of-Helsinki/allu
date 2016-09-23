import {Component, OnDestroy, OnInit} from '@angular/core';

import {Event} from '../../../event/event';
import {EventListener} from '../../../event/event-listener';
import {Application} from '../../../model/application/application';
import {EventService} from '../../../event/event.service';
import {ApplicationSaveEvent} from '../../../event/save/application-save-event';
import {ApplicationAddedAnnounceEvent} from '../../../event/announce/application-added-announce-event';
import {LocationState} from '../../../service/application/location-state';


@Component({
  selector: 'promotion-event',
  viewProviders: [],
  template: require('./promotion-event.component.html'),
  styles: [
    require('./promotion-event.component.scss')
  ]
})

export class PromotionEventComponent implements EventListener, OnInit, OnDestroy {
  private application: Application;
  private events: Array<any>;
  private applicantType: Array<string>;
  private countries: Array<any>;
  private billingTypes: Array<any>;
  private noPriceReasons: Array<any>;

  constructor(private eventService: EventService, locationState: LocationState) {
    this.application = Application.prefilledApplication();
    this.application.location = locationState.location;
  };

  ngOnInit(): any {
    this.eventService.subscribe(this);
  }

  ngOnDestroy(): any {
    this.eventService.unsubscribe(this);
  }

  public handle(event: Event): void {
    if (event instanceof ApplicationAddedAnnounceEvent) {
      let aaaEvent = <ApplicationAddedAnnounceEvent>event;
      console.log('Successfully added new application', aaaEvent.application);
    }
  }
}
