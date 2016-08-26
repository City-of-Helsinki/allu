import { Injectable } from '@angular/core';
import 'rxjs/add/operator/toPromise';

import {Task} from '../../service/task/task';
import {EventService} from '../../event/event.service';
import {EventListener} from '../../event/event-listener';
import {Event} from '../../event/event';
import {ErrorEvent} from '../../event/error-event';
import {GeolocationService} from '../../service/geolocation.service';
import {GeocoordinatesLoadEvent} from '../../event/load/geocoordinates-load-event';
import {GeoCoordinatesAnnounceEvent} from '../../event/announce/geocoordinates-announce-event';
import {Geocoordinates} from '../../model/common/geocoordinates';


@Injectable()
export class LoadGeocoordinatesTask extends Task {

  constructor(private geoLocationService: GeolocationService) {
    super();
  }

  protected createTask(runner: EventListener, eventService: EventService, event: Event): Promise<void> {
    let coordinatesEvent = <GeocoordinatesLoadEvent>event;
    return this.geoLocationService.geocode(coordinatesEvent.address)
      .toPromise()
      .then((coordinates: Geocoordinates) => {
        let gcaEvent = new GeoCoordinatesAnnounceEvent(coordinates);
        eventService.send(runner, gcaEvent);
      }).catch((err: any) => { eventService.send(runner, new ErrorEvent(coordinatesEvent)); });
  }
}
