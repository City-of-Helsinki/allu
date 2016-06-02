import {AnnounceEvent} from './announce-event';
import {Application} from '../../model/application/application';

export class ShapeAnnounceEvent extends AnnounceEvent {
  constructor(public shape: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>) {
    super();
  }
}
