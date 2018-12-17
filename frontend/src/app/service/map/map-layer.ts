import {FeatureGroup, Layer} from 'leaflet';

export class MapLayer {
  constructor(
    readonly id: string,
    readonly layer: Layer | FeatureGroup
  ) {}
}
