import {BackendFixedLocationSection} from './backend-fixed-location-section';
export interface BackendFixedLocationArea {
  id: number;
  name: string;
  sections: Array<BackendFixedLocationSection>;
}
