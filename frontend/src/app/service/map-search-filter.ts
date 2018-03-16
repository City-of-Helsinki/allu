import {LatLngBounds} from 'leaflet';
import {ApplicationStatus, ApplicationStatusGroup} from '../model/application/application-status';

export interface MapSearchFilter {
  address?: string;
  startDate?: Date;
  endDate?: Date;
  statuses?: Array<string>;
  geometry?: LatLngBounds;
}

export const defaultFilter = {
  address: undefined,
  startDate: undefined,
  endDate: undefined,
  statuses: [
    ApplicationStatusGroup.PRELIMINARY,
    ApplicationStatusGroup.HANDLING,
    ApplicationStatusGroup.DECISION
  ].map(sg => ApplicationStatusGroup[sg])
};
