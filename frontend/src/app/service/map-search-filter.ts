import {LatLngBounds} from 'leaflet';
import {ApplicationStatusGroup} from '@model/application/application-status';
import {applicationLayers} from '@feature/map/map-layer.service';
import {ApplicationType} from '@model/application/type/application-type';

export interface MapSearchFilter {
  startDate?: Date;
  endDate?: Date;
  statuses?: ApplicationStatusGroup[];
  types?: ApplicationType[];
  geometry?: LatLngBounds;
  layers?: string[];
  zoom?: number;
}

export const defaultFilter = {
  startDate: undefined,
  endDate: undefined,
  statuses: [
    ApplicationStatusGroup.PRELIMINARY,
    ApplicationStatusGroup.HANDLING,
    ApplicationStatusGroup.DECISION
  ],
  types: Object.keys(ApplicationType).map(type => ApplicationType[type]),
  layers: ['Karttasarja'].concat(applicationLayers),
  zoom: 6 // TODO get actual default used by map search component
};
