import 'leaflet';
import 'leaflet-draw';
import 'proj4leaflet';
import {Component, Input} from '@angular/core';
import {MapUtil} from '../../service/map.util.ts';
import {GeocodingService} from '../../service/geocoding.service';

import {EventListener} from '../../event/event-listener';
import {Event} from '../../event/event';
import {EventService} from '../../event/event.service';

import {ApplicationSelectionEvent} from '../../event/selection/application-selection-event';
import {ShapeAnnounceEvent} from '../../event/announce/shape-announce-event';
import {WorkqueueService} from '../../service/workqueue.service';
import {Map} from 'leaflet';
import {GeocoordinatesSelectionEvent} from '../../event/selection/geocoordinates-selection-event';
import {GeoCoordinatesAnnounceEvent} from '../../event/announce/geocoordinates-announce-event';
import {GeocoordinatesLoadEvent} from '../../event/load/geocoordinates-load-event';
import {SearchbarUpdateEvent} from '../../event/search/searchbar-updated-event';
import {MapHub} from '../../service/map-hub';
import {Geocoordinates} from '../../model/common/geocoordinates';
import {ApplicationHub} from '../../service/application-hub';
import {Application} from '../../model/application/application';

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
    private mapService: MapUtil,
    private geocoder: GeocodingService,
    private workqueue: WorkqueueService,
    private eventService: EventService,
    private mapHub: MapHub,
    private applicationHub: ApplicationHub) {
    this.eventService.subscribe(this);
    this.mapService = mapService;
    this.geocoder = geocoder;
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
    } else if (event instanceof SearchbarUpdateEvent) {
      this.eventService.send(this, new GeocoordinatesLoadEvent(event.searchbarFilter.search));
    }
  }

  handleApplicationSelectionEvent(event: ApplicationSelectionEvent): void {
    let asEvent = <ApplicationSelectionEvent>event;
    if (this.applicationArea) {
      this.map.removeLayer(this.applicationArea);
    }

    this.clearDrawn();

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

  ngOnInit() {
    this.initMap();
    this.mapHub.coordinates().subscribe((coordinates) => this.panToCoordinates(coordinates));
    this.applicationHub.applications().subscribe(applications => this.drawApplications(applications));
    this.applicationHub.addMapView(this.getCurrentMapView()); // to notify initial location
  }

  ngOnDestroy() {
    // TODO: See how to destroy map, so that it will be built again.
    this.eventService.unsubscribe(this);
    if (this.map) {
      this.map.removeLayer(this.mapLayers.kaupunkikartta);
    }
  }

  private drawApplications(applications: Array<Application>) {
    this.clearDrawn();

    applications
      .filter(app => app.location !== undefined)
      .forEach(app => this.drawGeometry(app.location.geometry));
  }

  private drawGeometry(geometryCollection: GeoJSON.GeometryCollection) {
    if (geometryCollection.geometries.length) {
      let featureCollection = this.mapService.geometryCollectionToFeatureCollection(geometryCollection);
      this.applicationArea = new L.GeoJSON(featureCollection);
      this.applicationArea.eachLayer((layer) => {
        this.drawnItems.addLayer(layer);
      });
    }
  }

  private clearDrawn() {
    if (this.drawnItems) {
      this.drawnItems.clearLayers();
    }
  }

  private initMap(): void {
    let mapOption = {
      zoomControl: false,
      center: new L.LatLng(60.1708763, 24.9424988), // Helsinki railway station
      scrollWheelZoom: this.zoom,
      zoom: 6,
      minZoom: 3,
      maxZoom: 13,
      maxBounds:
        new L.LatLngBounds(new L.LatLng(59.9084989595170114, 24.4555930248625906), new L.LatLng(60.4122137731072542, 25.2903558783246289)),
      layers: [this.mapLayers.kaupunkikartta],
      crs: this.mapService.getEPSG3879()
    };

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

    this.map.on('moveend', (e: any) => {
      this.applicationHub.addMapView(this.getCurrentMapView());
    });

    this.drawnItems = drawnItems;
    L.control.layers(this.mapLayers).addTo(this.map);
    L.control.scale().addTo(this.map);
  }

  private getCurrentMapView(): GeoJSON.GeometryObject {
    let mapView = this.mapService.polygonFromBounds(this.map.getBounds()).toGeoJSON();
    return this.mapService.featureToGeometry(mapView);
  }

  private panToCoordinates(coordinates: Geocoordinates) {
    const zoomLevel = 10;
    this.map.setView(new L.LatLng(coordinates.latitude, coordinates.longitude), zoomLevel, {animate: true});
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
