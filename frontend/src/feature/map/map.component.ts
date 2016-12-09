import {Component, Input, Output, EventEmitter, OnInit, OnDestroy} from '@angular/core';
import 'leaflet';
import 'leaflet-draw';
import 'proj4leaflet';
import {Map} from 'leaflet';

import {MapUtil} from '../../service/map.util.ts';
import {MapHub} from '../../service/map-hub';
import {Geocoordinates} from '../../model/common/geocoordinates';
import {Application} from '../../model/application/application';
import {FixedLocation} from '../../model/common/fixed-location';
import {Some} from '../../util/option';
import {ApplicationHub} from '../../service/application/application-hub';


@Component({
  selector: 'map',
  template: require('./map.component.html'),
  styles: [
    require('./map.component.scss')
  ]
})
export class MapComponent implements OnInit, OnDestroy {
  @Input() draw: boolean;
  @Input() edit: boolean;
  @Input() zoom: boolean;
  @Input() selection: boolean;
  @Input() applicationId: Number;
  @Input() showOnlyApplicationArea: boolean = false;

  @Output() editedItemCountChanged = new EventEmitter<number>();

  private applicationArea: L.LayerGroup<L.ILayer>;
  private map: Map;
  private mapLayers: any;
  private drawControl: L.Control.Draw;
  private drawnItems: L.FeatureGroup<L.ILayer>;
  private editedItems: L.FeatureGroup<L.ILayer>;

  constructor(
    private mapService: MapUtil,
    private mapHub: MapHub,
    private applicationHub: ApplicationHub) {
    this.mapLayers = this.createLayers();
    this.applicationArea = undefined;
    this.zoom = false;
    this.draw = false;
    this.selection = false;
  }

  ngOnInit() {
    this.initMap();
    this.mapHub.coordinates()
      .subscribe((optCoords) =>
        optCoords.map(coordinates => this.panToCoordinates(coordinates)));

    this.mapHub.applications().subscribe(applications => this.drawApplications(applications));
    this.mapHub.addMapView(this.getCurrentMapView()); // to notify initial location
    this.mapHub.applicationSelection().subscribe(app => this.applicationSelected(app));
    this.mapHub.selectedFixedLocations().subscribe(fxs => this.drawFixedLocations(fxs));
    // Handle fetching and drawing edited application as separate case
    Some(this.applicationId).do(id => this.applicationHub.getApplication(id).subscribe(app => this.drawEditedApplication(app)));
  }

  ngOnDestroy() {
    if (this.map) {
      this.map.removeLayer(this.mapLayers.kaupunkikartta);
    }
  }

  applicationSelected(application: Application) {
    if (this.applicationArea) {
      this.map.removeLayer(this.applicationArea);
    }

    this.clearDrawn();

    // Check to see if the application has a location
    if (application.hasGeometry()) {
      let featureCollection = this.mapService.geometryCollectionToFeatureCollection(application.location.geometry);
      this.applicationArea = new L.GeoJSON(featureCollection);
      this.applicationArea.eachLayer((layer) => {
        this.drawnItems.addLayer(layer);
      });

      this.map.fitBounds(L.latLngBounds(this.drawnItems.getBounds()));
    }
  }


  private drawApplications(applications: Array<Application>) {
    this.clearDrawn();

    let applicationShouldBeDrawn = (application: Application) =>
      !this.showOnlyApplicationArea || (this.showOnlyApplicationArea && application.id === this.applicationId);

    applications
      .filter(app => app.location !== undefined)
      .filter(app => applicationShouldBeDrawn(app))
      .filter(app => app.id !== this.applicationId) // Only draw other than edited application
      .forEach(app => this.drawGeometry(app.location.geometry, this.drawnItems));
  }

  private drawEditedApplication(application: Application) {
    this.drawGeometry(application.location.geometry, this.editedItems);
    this.updateMapControls(application);
    this.mapHub.addShape(this.editedItems.toGeoJSON());
  }

  private updateMapControls(application: Application) {
    if (application.hasFixedGeometry()) {
      this.setDynamicControls(false, this.editedItems);
    } else {
      this.editedItemCountChanged.emit(application.geometryCount());
    }
  }

  private drawFixedLocations(fixedLocations: Array<FixedLocation>) {
    Some(this.editedItems).do(edited => edited.clearLayers());
    fixedLocations.forEach(fx => this.drawGeometry(fx.geometry, this.editedItems));
    this.mapHub.addShape(this.editedItems.toGeoJSON());

    // Disable editing map with draw controls when we have fixed locations
    this.setDynamicControls(fixedLocations.length === 0, this.editedItems);

    Some(this.editedItems.getBounds())
      .filter(bounds => Object.keys(bounds).length > 0) // has some bounds
      .do(bounds => this.map.fitBounds(bounds));
  }

  private drawGeometry(geometryCollection: GeoJSON.GeometryCollection, drawLayer: L.LayerGroup<L.ILayer>, style?: Object) {
    if (geometryCollection.geometries.length) {
      let featureCollection = this.mapService.geometryCollectionToFeatureCollection(geometryCollection);
      this.applicationArea = new L.GeoJSON(featureCollection, style);
      this.applicationArea.eachLayer((layer) => {
        drawLayer.addLayer(layer);
      });
    }
  }

  private clearDrawn() {
    Some(this.drawnItems).do(fxs => fxs.clearLayers());
  }

  private initMap(): void {
    this.map = this.createMap();

    let drawnItems = new L.FeatureGroup();
    let editedItems = new L.FeatureGroup();
    drawnItems.addTo(this.map);
    editedItems.addTo(this.map);

    this.setDynamicControls(false, editedItems);

    let self = this;
    this.map.on('draw:created', function (e: any) {
      editedItems.addLayer(e.layer);
      self.addShape(editedItems);
    });

    this.map.on('draw:edited', function (e: any) {
      self.addShape(editedItems);
    });

    this.map.on('draw:deleted', function (e: any) {
      self.addShape(editedItems);
    });

    this.map.on('moveend', (e: any) => {
      if (!self.showOnlyApplicationArea) {
        self.mapHub.addMapView(this.getCurrentMapView());
      }
    });

    this.drawnItems = drawnItems;
    this.editedItems = editedItems;

    L.control.layers(this.mapLayers).addTo(this.map);
    L.control.zoom({position: 'topright'}).addTo(this.map);
    L.control.scale().addTo(this.map);
    L.Icon.Default.imagePath = '/assets/images';
  }

  private addShape(features: L.FeatureGroup<L.ILayer>) {
    let shape = features.toGeoJSON();
    this.mapHub.addShape(shape);
    this.editedItemCountChanged.emit(shape.features.length);
  }

  private createMap(): L.Map {
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

    return new L.Map('map', mapOption);
  }

  private setDynamicControls(controlsEnabled: boolean, editedItems: L.FeatureGroup<L.ILayer>): void {
    let draw = controlsEnabled ? {
      polygon: {
        shapeOptions: {
          color: '#BA1200'
        },
        allowIntersection: false,
        showArea: true
      },
      marker: false
    } : false;

    let drawControl = new L.Control.Draw({
      position: 'topright',
      draw: draw,
      edit: {
        featureGroup: editedItems,
        edit: controlsEnabled,
        remove: controlsEnabled
      }
    });

    if (this.draw) {
      // remove old control
      Some(this.drawControl).do(control => this.map.removeControl(control));
      this.map.addControl(drawControl);
      this.drawControl = drawControl;
    }
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
