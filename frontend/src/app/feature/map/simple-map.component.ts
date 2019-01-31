import {AfterViewInit, Component, Input, OnDestroy} from '@angular/core';
import * as L from 'leaflet';
import 'proj4leaflet';
import {MapUtil} from '@service/map/map.util';
import {DEFAULT_OVERLAY, MapLayerService} from '@feature/map/map-layer.service';
import {MapFeature} from '@feature/map/map-feature';
import {pathStyle} from '@service/map/map-draw-styles';

@Component({
  selector: 'simple-map',
  templateUrl: './simple-map.component.html',
  styleUrls: []
})
export class SimpleMapComponent implements AfterViewInit, OnDestroy {
  @Input() mapId = 'map';
  @Input() content: MapFeature[] = [];
  @Input() selectedFeature: number;

  _map: L.Map;
  _contentFeatures: L.FeatureGroup = new L.FeatureGroup();

  constructor(
    private mapUtil: MapUtil,
    private mapLayerService: MapLayerService) {}

  ngAfterViewInit(): void {
    this.createMap();
    this.addContent();
    this.centerAndZoomOnDrawn();
  }

  ngOnDestroy(): void {
    this._map.remove();
    this._map = undefined;
  }

  private createMap(): void {
    const mapOption = {
      zoomControl: false,
      dragging: false,
      scrollWheelZoom: false,
      zoom: 8,
      maxBounds: L.latLngBounds(
        L.latLng(59.9084989595170114, 24.4555930248625906),
        L.latLng(60.4122137731072542, 25.2903558783246289)
      ),
      crs: this.mapUtil.EPSG3879,
      continuousWorld: true,
      worldCopyJump: false
    };
    this._map = L.map(this.mapId, mapOption);

    this.mapLayerService.createOverlay(DEFAULT_OVERLAY).addTo(this._map);
    this._contentFeatures.addTo(this._map);
  }

  private addContent(): void {
    this.content
    .map(feature => this.featureToGeoJSON(feature))
    .forEach(geoJSON => this._contentFeatures.addLayer(geoJSON));
  }

  private featureToGeoJSON(feature: MapFeature): L.GeoJSON {
    const fc = this.mapUtil.geometryCollectionToFeatureCollection(feature.geometry);
    if (feature.id === this.selectedFeature) {
      return L.geoJSON(fc, {style: () => pathStyle.HIGHLIGHT});
    } else {
      return L.geoJSON(fc, {style: () => feature.style});
    }
  }

  private centerAndZoomOnDrawn() {
    const bounds = this._contentFeatures.getBounds();
    if (Object.keys(bounds).length > 0) {
      this._map.fitBounds(bounds);
    }
  }

}
