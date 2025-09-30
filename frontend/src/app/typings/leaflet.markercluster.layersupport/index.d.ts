import 'leaflet';
import 'leaflet.markercluster';

declare module 'leaflet' {
  interface MarkerClusterGroup {
    /**
     * Adds layers into the MarkerClusterGroup, but does not trigger re-clustering.
     * Provided by leaflet.markercluster.layersupport plugin.
     */
    checkIn(layer: Layer | Layer[]): this;

    /**
     * Removes layers from the MarkerClusterGroup, but does not trigger re-clustering.
     */
    checkOut(layer: Layer | Layer[]): this;
  }

  namespace markerClusterGroup {
    function layerSupport(options?: L.MarkerClusterGroupOptions): L.MarkerClusterGroup;
  }
}
