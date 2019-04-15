import {Layer} from 'leaflet';
import {ApplicationType} from '@model/application/type/application-type';

export class MapLayer {
  constructor(
    readonly id: string,
    readonly layer: Layer,
    readonly applicationType?: ApplicationType
  ) {}
}
