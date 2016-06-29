import 'leaflet';
import 'leaflet-draw';
import 'proj4leaflet';
import {Component} from '@angular/core';
import {MapService} from '../../service/map.service';
import {GeocodingService} from '../../service/geocoding.service';

import {EventListener} from '../../event/event-listener';
import {Event} from '../../event/event';
import {EventService} from '../../event/event.service';

import {ApplicationSelectionEvent} from '../../event/selection/application-selection-event';
import {ShapeAnnounceEvent} from '../../event/announce/shape-announce-event';
import {WorkqueueService} from '../../service/workqueue.service';
import {Map} from 'leaflet';

@Component({
  selector: 'map',
  moduleId: module.id,
  template: require('./map.component.html'),
  styles: [
    require('./map.component.scss')
  ]
})
export class MapComponent implements EventListener {

  private applicationArea: L.LayerGroup<L.ILayer>;
  private map: Map;
  private mapLayers: any;

  constructor(
    private mapService: MapService,
    private geocoder: GeocodingService,
    private workqueue: WorkqueueService,
    private eventService: EventService) {
    this.eventService.subscribe(this);
    this.mapService = mapService;
    this.geocoder = geocoder;
    this.workqueue = workqueue;
    this.mapLayers = this.createLayers();
    this.applicationArea = undefined;
  }

  public handle(event: Event): void {
    if (event instanceof ApplicationSelectionEvent) {
      let asEvent = <ApplicationSelectionEvent>event;
      if (this.applicationArea) {
        this.map.removeLayer(this.applicationArea);
      }
      let featureCollection = this.mapService.geometryCollectionToFeatureCollection(asEvent.application.location.geometry);
      this.applicationArea = new L.GeoJSON(featureCollection).addTo(this.map);
    }
  }

  ngOnInit() {

    this.map = new L.Map('map', {
      zoomControl: false,
      center: new L.LatLng(60.1708763, 24.9424988), // Helsinki railway station
      zoom: 6,
      minZoom: 3,
      maxZoom: 13,
      maxBounds:
        new L.LatLngBounds(new L.LatLng(59.9084989595170114, 24.4555930248625906), new L.LatLng(60.4122137731072542, 25.2903558783246289)),
      layers: [this.mapLayers.kaupunkikartta],
      crs: this.mapService.getEPSG3879()
    });
    L.control.zoom({position: 'topright'}).addTo(this.map);

    let drawnItems = new L.GeoJSON();
    this.map.addLayer(drawnItems);

    let drawControl = new L.Control.Draw({
      position: 'topright',
      draw: {
        polygon: {
          shapeOptions: {
            color: '#BA1200'
          }
        },
        marker: false
      },
      edit: {
        featureGroup: drawnItems
      }
    });
    this.map.addControl(drawControl);
    let that = this;
    this.map.on('draw:created', function (e: any) {
      let type = e.layerType,
        layer = e.layer;

      drawnItems.addLayer(layer);
      that.eventService.send(that, new ShapeAnnounceEvent(drawnItems.toGeoJSON()));
    });

    // Add zoom controls

    L.control.layers(this.mapLayers).addTo(this.map);
    L.control.scale().addTo(this.map);
  }

  ngOnDestroy() {
    // TODO: See how to destroy map, so that it will be built again.
    this.eventService.unsubscribe(this);
    if (this.map) {
      this.map.removeLayer(this.mapLayers.kaupunkikartta);
    }
  }

  private createLayers(): any {
    return {
      kaupunkikartta: new L.TileLayer.WMS('/wms?',
        {layers: 'helsinki_kaupunkikartta', format: 'image/png'})
      // working URL for accessing Helsinki maps directly (requires authentication)
      // testi: new L.TileLayer.WMS('http://kartta.hel.fi/ws/geoserver/helsinki/wms?helsinki',
      //   {layers: 'helsinki:Kaupunkikartta'}),
      // TMS works, but unfortunately seems to use somehow invalid CRS. Thus, WMS is used. Left here for possible future use
      // testi: new L.TileLayer('http://10.176.127.67:8080/tms/1.0.0/helsinki_kaupunkikartta/EPSG_3879/{z}/{x}/{y}.png',
      //   { tms: true }),
    };

  }
}
