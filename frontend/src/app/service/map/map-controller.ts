import * as L from 'leaflet';
import 'leaflet-draw';
import 'proj4leaflet';
import 'leaflet-groupedlayercontrol';
import 'leaflet-measure-path';
import '../../js/leaflet/draw-transform';
import '../../js/leaflet/draw-intersect';
import '../../js/leaflet/draw-line';

import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';
import {MapUtil} from './map.util';
import {Some} from '../../util/option';
import {translations} from '../../util/translations';
import {Geocoordinates} from '../../model/common/geocoordinates';
import {MapLayerService} from './map-layer.service';
import {NotificationService} from '../notification/notification.service';
import {drawOptions, editOptions} from './map-config';
import {MapStore} from './map-store';
import GeoJSONOptions = L.GeoJSONOptions;
import {MapEventHandler} from './map-event-handler';
import {MapFeatureInfo} from './map-feature-info';
import {MapPopupService} from './map-popup.service';

const alluIcon = L.icon({
  iconUrl: 'assets/images/marker-icon.png',
  shadowUrl: undefined
});

export class ShapeAdded {
  constructor(public features: L.FeatureGroup, public affectsControls: boolean = true) {
  }
}

export interface MapControllerConfig {
  draw: boolean;
  edit: boolean;
  zoom: boolean;
  selection: boolean;
  showOnlyApplicationArea;
}

export class MapController {
  private map: L.Map;
  private drawControl: L.Control.Draw;
  private focusedItems: L.FeatureGroup;
  private editedItems: L.FeatureGroup;
  private shapes$ = new Subject<ShapeAdded>();
  private editing = false;
  private deleting = false;
  private destroy = new Subject<boolean>();

  constructor(private mapUtil: MapUtil,
              private mapStore: MapStore,
              private mapLayerService: MapLayerService,
              private popupService: MapPopupService,
              private config: MapControllerConfig) {
    this.initMap();
    this.handleDrawingAllowedChanges();
  }

  onDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  public clearDrawn() {
    this.mapLayerService.contentLayerArray
      .forEach(featureGroup => featureGroup.clearLayers());
  }

  public clearEdited() {
    Some(this.editedItems).do(edited => edited.clearLayers());
  }

  public clearFocused() {
    Some(this.focusedItems).do(layer => layer.clearLayers());
  }

  public panToCoordinates(coordinates: Geocoordinates) {
    const zoomLevel = 10;
    this.map.setView(L.latLng(coordinates.latitude, coordinates.longitude), zoomLevel, {animate: true});
  }

  public centerAndZoomOnDrawn() {
    Some(this.drawLayers())
      .filter(items => Object.keys(items.getBounds()).length !== 0)
      .map(items => items.getBounds())
      .do(bounds => this.map.fitBounds(bounds));
  }

  public setDynamicControls(controlsEnabled: boolean, editedItems?: L.FeatureGroup): void {
    const items = editedItems || this.editedItems;

    const drawControl = new L.Control.Draw({
      position: 'topright',
      draw: drawOptions(controlsEnabled),
      intersectLayers: this.mapLayerService.contentLayerArray,
      edit: {
        featureGroup: items,
        edit: editOptions(controlsEnabled),
        remove: controlsEnabled
      }
    });
    this.setLocalizations();

    if (this.config.draw) {
      // remove old control
      Some(this.drawControl).do(control => this.map.removeControl(control));
      this.map.addControl(drawControl);
      this.drawControl = drawControl;
    }
  }

  public drawGeometry(geometries: Array<GeoJSON.GeometryCollection>, layerName: string,
                      style?: Object, featureInfo?: MapFeatureInfo) {
    const layer = this.mapLayerService.contentLayers[layerName];
    if (layer) {
      geometries.forEach(g => this.drawGeometryToLayer(g, layer, style, featureInfo));
    } else {
      throw new Error('No draw layer with name ' + layerName);
    }
  }

  public drawFocused(geometries: Array<GeoJSON.GeometryCollection>, style?: Object): void {
    geometries.forEach(g => this.drawGeometryToLayer(g, this.focusedItems, style));
  }

  public drawFixedLocations(geometries: Array<GeoJSON.GeometryCollection>, style?: Object) {
    geometries.forEach(geometry => this.drawEditableGeometry(geometry, style));
    this.shapes$.next(new ShapeAdded(this.editedItems, false));
  }

  public drawEditableGeometry(geometry: GeoJSON.GeometryCollection, style?: Object) {
    if (geometry) {
      this.drawGeometryToLayer(geometry, this.editedItems, style);
      this.showMeasurements(this.editedItems);
      this.editedItems.bringToFront();
    }
  }

  public fitEditedToView() {
    Some(this.editedItems.getBounds())
      .filter(bounds => Object.keys(bounds).length > 0) // has some bounds
      .do(bounds => this.map.fitBounds(bounds));
  }

  // For some reason leaflet does not show layer after angular route change
  // unless it is removed and added back
  selectDefaultLayer(): void {
    this.map.removeLayer(this.mapLayerService.defaultOverlay);
    this.map.addLayer(this.mapLayerService.defaultOverlay);
  }

  get shapes(): Observable<ShapeAdded> {
    return this.shapes$.asObservable();
  }

  private drawGeometryToLayer(geometryCollection: GeoJSON.GeometryCollection,
                              drawLayer: L.LayerGroup,
                              style?: GeoJSONOptions, featureInfo?: MapFeatureInfo) {
    if (geometryCollection.geometries.length) {
      style = style || {};
      const featureCollection = this.mapUtil.geometryCollectionToFeatureCollection(geometryCollection, featureInfo);
      style.pointToLayer = (point, latlng) => L.marker(latlng, {icon: alluIcon})
        .bindPopup((layer: any) => this.popupService.create([layer.feature]), {className: 'allu-map-popup'});
      const geoJSON = L.geoJSON(featureCollection, style);
      this.drawGeoJSON(geoJSON, drawLayer);
    }
  }

  private drawGeoJSON(geoJSON: L.GeoJSON, drawLayer: L.LayerGroup): void {
    geoJSON.eachLayer((l: any) => {
      drawLayer.addLayer(l);
    });
  }

  private initMap(): void {
    this.map = this.createMap();
    this.mapStore.mapViewChange(this.map.getBounds());

    const editedItems = L.featureGroup();
    editedItems.addTo(this.map);
    this.editedItems = editedItems;

    this.setupEventHandling(editedItems);
    this.setupLayerControls();

    L.control.zoom({
      position: 'topright',
      zoomInTitle: translations.map.zoomIn,
      zoomOutTitle: translations.map.zoomOut
    }).addTo(this.map);
    L.control.scale().addTo(this.map);
    L.Icon.Default['imagePath'] = '/assets/images/';
    this.setDynamicControls(this.mapStore.snapshot.drawingAllowed, editedItems);
  }

  private createMap(): L.Map {
    const mapOption = {
      zoomControl: false,
      center: L.latLng(60.1708763, 24.9424988), // Helsinki railway station
      scrollWheelZoom: this.config.zoom,
      zoom: 6,
      minZoom: 3,
      maxZoom: 13,
      maxBounds:
        L.latLngBounds(L.latLng(59.9084989595170114, 24.4555930248625906), L.latLng(60.4122137731072542, 25.2903558783246289)),
      layers: this.mapLayerService.initialLayers,
      crs: this.mapUtil.EPSG3879,
      continuousWorld: true,
      worldCopyJump: false
    };
    return L.map('map', mapOption);
  }

  private setupEventHandling(editedItems: L.FeatureGroup): void {
    const self = this;
    this.map.on('draw:created', (e: any) => {
      editedItems.addLayer(e.layer);
      self.shapes$.next(new ShapeAdded(editedItems));
      e.layer.showMeasurements(translations.map.measure);
    });

    this.map.on('draw:edited', (e: any) => self.shapes$.next(new ShapeAdded(editedItems)));
    this.map.on('draw:deleted', (e: any) => self.shapes$.next(new ShapeAdded(editedItems)));

    this.map.on('draw:editstart', () => {
      this.editing = true;
      self.editedItems.bringToFront();
    });

    this.map.on('draw:editstop', () => self.editing = false);

    this.map.on('draw:deletestart', () => {
      self.deleting = true;
      self.editedItems.bringToFront();
    });

    this.map.on('draw:deletestop', () => self.deleting = false);

    this.map.on('moveend', (e: any) => {
      if (!self.config.showOnlyApplicationArea) {
        self.mapStore.mapViewChange(this.map.getBounds());
      }
    });

    this.map.on(L.Draw.Event.INTERSECTS, (e: any) => {
      NotificationService.errorMessage(translations.map.areasIntersect, 2000);
    });

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      if (!(this.editing || this.deleting)) {
        self.showTooltipOnClick(e);
      }
    });

    this.mapLayerService.cityDistricts.on('load', (e: any) => this.addCityDistrictLabels(e.layers));
  }

  private setupLayerControls(): void {
    const groupedOverlays = {
      'Karttatasot': this.mapLayerService.overlays,
      'Hakemustyypit': this.mapLayerService.contentLayers,
      'Winkin katutyöt': this.mapLayerService.winkkiRoadWorks,
      'Winkin vuokraukset ja tapahtumat': this.mapLayerService.winkkiEvents,
      'Muut': this.mapLayerService.other
    };
    const groupedControl = L.control.groupedLayers(undefined, groupedOverlays);

    // Add marker group support for application layers
    this.mapLayerService.markerSupport.addTo(this.map);
    this.mapLayerService.markerSupport.checkIn(this.mapLayerService.contentLayerArray);

    // Add layer group control to map
    groupedControl.addTo(this.map);

    // Need to add content layers (applications) to map again to pre-select
    // them from layer selection control
    this.mapLayerService.contentLayerArray.forEach(l => l.addTo(this.map));

    this.focusedItems = L.featureGroup();
    this.focusedItems.addTo(this.map);

    this.mapLayerService.restrictedOverlays.subscribe(restricted => {
      Object.keys(restricted).forEach(key => {
        groupedControl.addOverlay(restricted[key], key, 'Karttatasot');
      });
    });
  }

  private setLocalizations(): void {
    // Need to cast as any since ES6 module declaration exports variables
    // as constants so you cannot assign to them
    (<any>L).drawLocal = translations.map;
  }

  private drawLayers(): L.FeatureGroup {
    return this.mapLayerService.contentLayerArray
      .reduce((allLayers, currentLayer) => {
        allLayers.addLayer(currentLayer);
        return allLayers;
      }, L.featureGroup());
  }

  private showMeasurements(layers: L.FeatureGroup) {
    layers.eachLayer((l: any) => {
      if (l.feature.geometry.type !== 'Point') {
        l.showMeasurements(translations.map.measure);
      }
    });
  }

  private showTooltipOnClick(e: L.LeafletMouseEvent): void {
    const intersecting = MapEventHandler.clickIntersects(e, this.map, this.mapLayerService.clickableLayers);
    if (intersecting.length) {
      const features = intersecting.map((l: any) => l.feature);
      L.popup({className: 'allu-map-popup'})
        .setLatLng(e.latlng)
        .setContent(this.popupService.create(features))
        .openOn(this.map);
    }
  }

  private addCityDistrictLabels(layers: any) {
    layers.eachLayer(layer => {
      const props = layer.feature.properties;
      const text = `${props.tunnus} ${props.nimi_fi}`;
      const center = layer.getBounds().getCenter();
      const myIcon = L.divIcon({html: text, className: 'allu-simple-label', iconAnchor: center});
      L.marker(center, {icon: myIcon}).addTo(this.mapLayerService.cityDistricts);
    });
  }

  private handleDrawingAllowedChanges(): void {
    const drawingAllowed = (drawing: boolean, sections: number[]) => {
      const noSelectedSections = sections === undefined || sections.length === 0;
      return drawing && noSelectedSections;
    };

    Observable.combineLatest(
      this.mapStore.drawingAllowed,
      this.mapStore.selectedSections,
      drawingAllowed
    ).takeUntil(this.destroy)
      .subscribe(allowed => this.setDynamicControls(allowed));
  }
}
