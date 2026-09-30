// Ambient type augmentation for leaflet.markercluster.layersupport.
// leaflet.markercluster augments the `leaflet` module with MarkerClusterGroup
// and MarkerClusterGroupOptions, so we reference those via the `leaflet` namespace.

import * as L from 'leaflet';
import 'leaflet.markercluster';

declare module 'leaflet' {
  interface MarkerClusterGroupLayerSupport extends L.MarkerClusterGroup {
    /**
     * Register one or more layers to be clustered by this MarkerClusterGroup.
     */
    checkIn(layers: L.Layer | L.Layer[]): this;
  }

  namespace markerClusterGroup {
    function layerSupport(options?: L.MarkerClusterGroupOptions): MarkerClusterGroupLayerSupport;
  }
}
