import {Injectable} from '@angular/core';

import {MapUtil} from './map.util';
import {MapLayerService} from './map-layer.service';
import {MapState, MapStateConfig} from './map-state';

@Injectable()
export class MapService {

  constructor(private mapUtil: MapUtil, private mapLayerService: MapLayerService) {}

  public create(
    draw: boolean = false,
    edit: boolean = false,
    zoom: boolean = false,
    selection: boolean = false,
    showOnlyApplicationArea: boolean = false
  ) {
    const config: MapStateConfig = {
      draw: draw,
      edit: edit,
      zoom: zoom,
      selection: selection,
      showOnlyApplicationArea: showOnlyApplicationArea
    };
    return new MapState(this.mapUtil, this.mapLayerService, config);
  }
}
