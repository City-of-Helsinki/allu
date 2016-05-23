import {SelectionEvent} from './selection-event';
import {LatLng} from '../../model/location/latlng';

export class ApplicationSelectionEvent extends SelectionEvent {
  constructor(public id: number) {
    super();
  }
}
