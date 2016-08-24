import 'leaflet';
import 'leaflet-draw';
import 'proj4leaflet';
import {Component, Input} from '@angular/core';
import {MapService} from '../../service/map.service';
import {GeocodingService} from '../../service/geocoding.service';
import {GeolocationService} from '../../service/geolocation.service';

import {EventListener} from '../../event/event-listener';
import {Event} from '../../event/event';
import {EventService} from '../../event/event.service';

import {ApplicationSelectionEvent} from '../../event/selection/application-selection-event';
import {ShapeAnnounceEvent} from '../../event/announce/shape-announce-event';
import {WorkqueueService} from '../../service/workqueue.service';
import {Map} from 'leaflet';
import {GeocoordinatesSelectionEvent} from '../../event/selection/geocoordinates-selection-event';

@Component({
  selector: 'map',
  moduleId: module.id,
  template: require('./map.component.html'),
  styles: [
    require('./map.component.scss')
  ]
})
export class MapComponent implements EventListener {
  @Input() draw: boolean;
  @Input() zoom: boolean;
  @Input() selection: boolean;
  @Input() edit: boolean;
  private applicationArea: L.LayerGroup<L.ILayer>;
  private map: Map;
  private mapLayers: any;
  private drawnItems: any;

  constructor(
    private mapService: MapService,
    private geocoder: GeocodingService,
    private geolocationService: GeolocationService,
    private workqueue: WorkqueueService,
    private eventService: EventService) {
    this.eventService.subscribe(this);
    this.mapService = mapService;
    this.geocoder = geocoder;
    this.geolocationService = geolocationService;
    this.workqueue = workqueue;
    this.mapLayers = this.createLayers();
    this.applicationArea = undefined;
    this.zoom = false;
    this.draw = false;
    this.selection = false;
  }

  public handle(event: Event): void {
    if (event instanceof ApplicationSelectionEvent) {
      this.handleApplicationSelectionEvent(<ApplicationSelectionEvent>event);
    } else if (event instanceof GeocoordinatesSelectionEvent) {
      this.handleGeocoordinatesSelectionEvent(<GeocoordinatesSelectionEvent>event);
    }
  }

  handleApplicationSelectionEvent(event: ApplicationSelectionEvent): void {
    let asEvent = <ApplicationSelectionEvent>event;
    if (this.applicationArea) {
      this.map.removeLayer(this.applicationArea);
    }

    if (this.drawnItems) {
      this.drawnItems.clearLayers();
    }

    // Check to see if the application has a location
    console.log('asEvent.application.location', asEvent.application.location);
    if (asEvent.application.location && asEvent.application.location.geometry.geometries.length) {
      let featureCollection = this.mapService.geometryCollectionToFeatureCollection(asEvent.application.location.geometry);
      this.applicationArea = new L.GeoJSON(featureCollection);
      this.applicationArea.eachLayer((layer) => {
        this.drawnItems.addLayer(layer);
      });

      if (this.selection && featureCollection.features) {
        let bounds = new Array<L.LatLng> ();
        for (let feature of featureCollection.features) {
          for (let list of feature.geometry.coordinates) {
            for (let coordinates of list) {
              bounds.push(L.latLng(coordinates[1], coordinates[0]));
            }
          }
        }
        this.map.fitBounds(L.latLngBounds(bounds));
      }
    }
  }

  handleGeocoordinatesSelectionEvent(event: GeocoordinatesSelectionEvent) {
    let coordinates = new L.LatLng(event.geocoordinates.latitude, event.geocoordinates.longitude);
    const zoomLevel = 10;
    this.map.setView(coordinates, zoomLevel, {animate: true});
  }

  ngOnInit() {
    let mapOption = {
      zoomControl: false,
      center: undefined,
      scrollWheelZoom: this.zoom,
      zoom: 6,
      minZoom: 3,
      maxZoom: 13,
      maxBounds:
        new L.LatLngBounds(new L.LatLng(59.9084989595170114, 24.4555930248625906), new L.LatLng(60.4122137731072542, 25.2903558783246289)),
      layers: [this.mapLayers.kaupunkikartta],
      crs: this.mapService.getEPSG3879()
    };

    if (this.draw) {
      mapOption.center = new L.LatLng(60.1708763, 24.9424988); // Helsinki railway station
    }

    this.map = new L.Map('map', mapOption);
    L.control.zoom({position: 'topright'}).addTo(this.map);

    let drawnItems = new L.FeatureGroup();
    drawnItems.addTo(this.map);

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
    if (this.draw) {
      this.map.addControl(drawControl);
    }
    let that = this;
    this.map.on('draw:created', function (e: any) {
      let layer = e.layer;
      drawnItems.addLayer(layer);
      that.eventService.send(that, new ShapeAnnounceEvent(drawnItems.toGeoJSON()));
    });

    this.map.on('draw:edited', function (e: any) {
      that.eventService.send(that, new ShapeAnnounceEvent(drawnItems.toGeoJSON()));
    });

    this.map.on('draw:deleted', function (e: any) {
      that.eventService.send(that, new ShapeAnnounceEvent(drawnItems.toGeoJSON()));
    });

    this.drawnItems = drawnItems;
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
        {layers: 'helsinki_kaupunkikartta', format: 'image/png'}),
      ortoilmakuva: new L.TileLayer.WMS('/wms?',
        {layers: 'helsinki_ortoilmakuva', format: 'image/png'}),
      kiinteistokartta: new L.TileLayer.WMS('/wms?',
        {layers: 'helsinki_kiinteistokartta', format: 'image/png'}),
      ajantasaasemakaava: new L.TileLayer.WMS('/wms?',
        {layers: 'helsinki_ajantasaasemakaava', format: 'image/png'}),
      opaskartta: new L.TileLayer.WMS('/wms?',
        {layers: 'helsinki_opaskartta', format: 'image/png'}),
      kaupunginosajako: new L.TileLayer.WMS('/wms?',
        {layers: 'helsinki_kaupunginosajako', format: 'image/png'})
      // working URL for accessing Helsinki maps directly (requires authentication)
      // testi: new L.TileLayer.WMS('http://kartta.hel.fi/ws/geoserver/helsinki/wms?helsinki',
      //   {layers: 'helsinki:Kaupunkikartta'}),
      // TMS works, but unfortunately seems to use somehow invalid CRS. Thus, WMS is used. Left here for possible future use
      // testi: new L.TileLayer('http://10.176.127.67:8080/tms/1.0.0/helsinki_kaupunkikartta/EPSG_3879/{z}/{x}/{y}.png',
      //   { tms: true }),
    };

  }
}
