import {SelectionEvent} from './selection-event';
import {Geocoordinates} from '../../model/common/geocoordinates';

export class GeocoordinatesSelectionEvent extends SelectionEvent {

  constructor(public geocoordinates: Geocoordinates) {
    super();
  }
}
