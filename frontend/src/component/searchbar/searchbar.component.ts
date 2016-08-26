import {Component, Input, OnInit, OnDestroy, Output, EventEmitter} from '@angular/core';

import {EventListener} from '../../event/event-listener';
import {EventService} from '../../event/event.service';
import {Event} from '../../event/event';
import {GeocoordinatesLoadEvent} from '../../event/load/geocoordinates-load-event';
import {GeoCoordinatesAnnounceEvent} from '../../event/announce/geocoordinates-announce-event';
import {StringUtil} from '../../util/string.util';
import {GeocoordinatesSelectionEvent} from '../../event/selection/geocoordinates-selection-event';
import {MdToolbar} from '@angular2-material/toolbar';

@Component({
  selector: 'searchbar',
  moduleId: module.id,
  template: require('./searchbar.component.html'),
  styles: [
    require('./searchbar.component.scss')
  ],
  directives: [MdToolbar]
})

export class SearchbarComponent implements EventListener, OnInit, OnDestroy {
  @Input()
  search: string;

  @Output()
  searchUpdated = new EventEmitter();

  constructor(private eventService: EventService) {}

  ngOnInit() {
    this.eventService.subscribe(this);
  }

  ngOnDestroy() {
    this.eventService.unsubscribe(this);
  }

  public handle(event: Event): void {
    console.log('Handle and incoming SearchbarComponent event', event);
    if (event instanceof GeoCoordinatesAnnounceEvent) {
      let gcaEvent = <GeoCoordinatesAnnounceEvent>event;
      this.eventService.send(this, new GeocoordinatesSelectionEvent(gcaEvent.geocoordinates));
    }
  }

  searchLocation(): void {
    console.log('searchLocation');
    this.eventService.send(this, new GeocoordinatesLoadEvent(this.search));
    this.searchUpdated.emit(this.search);
  }
}
