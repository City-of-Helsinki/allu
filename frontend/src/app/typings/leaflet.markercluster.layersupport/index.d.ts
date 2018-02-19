import * as LMC from 'leaflet.markercluster';

declare module 'leaflet' {
  namespace markerClusterGroup {
    function layerSupport(options?: LMC.MarkerClusterGroupOptions): LMC.MarkerClusterGroup;
  }
}
