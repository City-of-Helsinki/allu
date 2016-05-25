// area: {
//   type: 'polygon',
//     latlngs: [
//     {lat: 60.158910597964926, lng: 24.955063462257385},

import {LatLng} from './latlng';
export class Area {

  constructor(public type: string, public latlngs: Array<LatLng>, public radius: number) {}
}
