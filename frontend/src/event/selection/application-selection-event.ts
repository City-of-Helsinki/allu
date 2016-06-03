import {SelectionEvent} from './selection-event';
import {LatLng} from '../../model/location/latlng';

export class ApplicationSelectionEvent extends SelectionEvent {
  constructor(public area: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>) {
    super();
  }
}
