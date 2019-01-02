import {LatLngBounds} from 'leaflet';
import {ApplicationStatusGroup} from '@model/application/application-status';
import {applicationLayers} from '@feature/map/map-layer.service';

export interface MapSearchFilter {
  address?: string;
  startDate?: Date;
  endDate?: Date;
  statuses?: ApplicationStatusGroup[];
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
  layers: ['Karttasarja'].concat(applicationLayers)
};
