import * as L from 'leaflet';

declare module 'leaflet' {
  export interface Circle<P = any> extends CircleMarker<P> {
    toPolygon(vertices?: number, map?: L.Map);
  }
}
