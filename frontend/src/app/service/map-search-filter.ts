import {LatLngBounds} from 'leaflet';
import {ApplicationStatus} from '../model/application/application-status';

export interface MapSearchFilter {
  address?: string;
  startDate?: Date;
  endDate?: Date;
  statusTypes?: Array<string>;
  geometry?: LatLngBounds;
}

export const defaultFilter = {
  address: undefined,
  startDate: undefined,
  endDate: undefined,
  statusTypes: [
    ApplicationStatus.PRE_RESERVED,
    ApplicationStatus.PENDING,
    ApplicationStatus.HANDLING,
    ApplicationStatus.RETURNED_TO_PREPARATION,
    ApplicationStatus.DECISIONMAKING,
    ApplicationStatus.DECISION
  ].map(status => ApplicationStatus[status])
};
