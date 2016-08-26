import {AnnounceEvent} from './announce-event';
import {Geocoordinates} from '../../model/common/geocoordinates';

export class GeoCoordinatesAnnounceEvent extends AnnounceEvent {
  constructor(public geocoordinates: Geocoordinates) {
    super();
  }
}
