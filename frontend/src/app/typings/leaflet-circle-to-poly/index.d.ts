import * as L from 'leaflet';

declare module 'leaflet' {
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- leaflet typings interop
  export interface Circle<P = any> extends CircleMarker<P> {
    toPolygon(vertices?: number, map?: L.Map);
  }
}
