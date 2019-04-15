import {LatLngBounds} from 'leaflet';
import {ApplicationStatusGroup} from '@model/application/application-status';
import {applicationLayers} from '@feature/map/map-layer.service';
import {ApplicationType} from '@model/application/type/application-type';

export interface MapSearchFilter {
  address?: string;
  startDate?: Date;
  endDate?: Date;
  statuses?: ApplicationStatusGroup[];
  types?: ApplicationType[];
  geometry?: LatLngBounds;
  layers?: string[];
}

export const defaultFilter = {
  address: undefined,
  startDate: undefined,
  endDate: undefined,
  statuses: [
    ApplicationStatusGroup.PRELIMINARY,
    ApplicationStatusGroup.HANDLING,
    ApplicationStatusGroup.DECISION
  ],
  types: Object.keys(ApplicationType).map(type => ApplicationType[type]),
  layers: ['Karttasarja'].concat(applicationLayers)
};
