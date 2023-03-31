import * as L from 'leaflet';
import 'leaflet.markercluster';

declare module 'leaflet' {
  namespace markerClusterGroup {
    export function layerSupport(options?: L.MarkerClusterGroupOptions): L.MarkerClusterGroup;
  }
}
