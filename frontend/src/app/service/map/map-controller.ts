import * as L from 'leaflet';
import 'leaflet-draw';
import 'proj4leaflet';
import 'leaflet-groupedlayercontrol';
import 'leaflet-measure-path';
import '../../js/leaflet/draw-transform';
import '../../js/leaflet/draw-intersect';
import '../../js/leaflet/draw-tools';
import '../../js/leaflet/draw-toolbar';

import {combineLatest, Observable, Subject} from 'rxjs';
import {MapUtil} from './map.util';
import {Some} from '@util/option';
import {translations} from '@util/translations';
import {Geocoordinates} from '@model/common/geocoordinates';
import {MapLayerService} from '@feature/map/map-layer.service';
import {NotificationService} from '@feature/notification/notification.service';
import {drawOptions, editOptions} from './map-config';
import {MapStore} from './map-store';
import {MapEventHandler} from './map-event-handler';
import {MapFeatureInfo} from './map-feature-info';
import {MapPopupService} from './map-popup.service';
import {Injectable} from '@angular/core';
import {distinctUntilChanged, filter, map, takeUntil} from 'rxjs/operators';
import {MapLayer} from '@service/map/map-layer';
import {BehaviorSubject} from 'rxjs/internal/BehaviorSubject';
import {FeatureCollection, GeometryObject} from 'geojson';
import {Projection} from '@feature/map/projection';
import GeoJSONOptions = L.GeoJSONOptions;
import {LatLngBounds} from 'leaflet';

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

@Injectable()
export class MapController {
  private map: L.Map;
  private drawControl: L.Control.Draw;
  private focusedItems: L.FeatureGroup;
  private editedItems: L.FeatureGroup;
  private shapes$ = new Subject<ShapeAdded>();
  private editing = false;
  private deleting = false;
  private destroy = new Subject<boolean>();
  private config: MapControllerConfig;
  private _allLayers$: BehaviorSubject<MapLayer[]> = new BehaviorSubject([]);
  private _selectedLayers$: BehaviorSubject<MapLayer[]> = new BehaviorSubject([]);

  constructor(private mapUtil: MapUtil,
              private projection: Projection,
              private mapStore: MapStore,
              private mapLayerService: MapLayerService,
              private popupService: MapPopupService,
              private notification: NotificationService) {
    combineLatest([
      this._allLayers$,
      this._selectedLayers$
    ]).pipe(
      takeUntil(this.destroy),
      filter(() => !!this.map)
    ).subscribe(([all, selected]) => this.selectLayers(all, selected));
  }

  init(config: MapControllerConfig) {
    this.config = config;
    this.initMap();
    this.handleDrawingAllowedChanges();
    this.reloadSelectedLayers();
  }

  remove(): void {
    this.map.remove();
    this.map = undefined;
  }

  public get availableLayers() {
    return this._allLayers$.getValue();
  }

  public set availableLayers(layers: MapLayer[]) {
    if (layers) {
      this._allLayers$.next(layers);
    }
  }

  public get selectedLayers() {
    return this._selectedLayers$.getValue();
  }

  public set selectedLayers(layers: MapLayer[]) {
    if (layers) {
      this._selectedLayers$.next(layers);
    }
  }

  public clearDrawn() {
    this.mapLayerService.contentLayerArray
      .forEach(fg => fg.clearLayers());
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

  public centerAndZoomOnEditedAndFocused() {
    let bounds = this.focusedItems.getBounds();
    bounds = bounds.extend(this.editedItems.getBounds());
    if (bounds.isValid()) {
      this.map.fitBounds(bounds);
    }
  }

  public setDynamicControls(controlsEnabled: boolean, editedItems?: L.FeatureGroup): void {
    const items = editedItems || this.editedItems;

    const drawControl = new L.Control.Draw({
      position: 'topright',
      draw: drawOptions(controlsEnabled),
      intersectLayers: this.mapLayerService.contentLayerArray,
      edit: editOptions(items, controlsEnabled)
    });
    this.setLocalizations();

    if (this.map && this.config.draw) {
      // remove old control
      Some(this.drawControl).do(control => this.map.removeControl(control));
      this.map.addControl(drawControl);
    }

    this.drawControl = drawControl;
  }

  public drawToLayer(layerName: string, featureCollection: FeatureCollection<GeometryObject>, style?: GeoJSONOptions) {
    const layer = this.mapLayerService.getContentLayer(layerName);
    layer.clearLayers();

    if (layer && featureCollection) {
      style.pointToLayer = (point, latlng) => L.marker(latlng, {icon: alluIcon})
        .bindPopup((l: any) => this.popupService.create([l.feature]), {className: 'allu-map-popup'});
      const geoJSON = L.geoJSON(featureCollection, style);
      this.drawGeoJSON(geoJSON, layer);
    }
  }

  public drawGeometry(geometries: Array<GeoJSON.GeometryCollection>, layerName: string,
                      style?: Object, featureInfo?: MapFeatureInfo) {
    const layer = this.mapLayerService.getContentLayer(layerName);
    if (layer) {
      geometries.forEach(g => this.drawGeometryToLayer(g, layer, style, featureInfo));
    } else {
      throw new Error('No draw layer with name ' + layerName);
    }
  }

  public drawFocused(geometries: Array<GeoJSON.GeometryCollection>, style?: Object): void {
    geometries.forEach(g => this.drawGeometryToLayer(g, this.focusedItems, style));
  }

  public drawFixedGeometries(geometries: Array<GeoJSON.GeometryCollection>, style?: Object) {
    geometries.forEach(geometry => this.drawEditableGeometry(geometry, style));
    this.shapes$.next(new ShapeAdded(this.editedItems, false));
  }

  public drawFeatures(featureCollection: FeatureCollection<GeometryObject>, style?: Object): void {
    this.drawFeaturesToLayer(featureCollection, this.editedItems, style);
    this.showMeasurements(this.editedItems);
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

  public savePending(): void {
    this.saveIfActive(this.drawControl.getToolbar('edit'));
  }

  private saveIfActive(control: L.EditToolbar): void {
    if (control && control.enabled()) {
      control.save();
    }
  }

  reloadSelectedLayers(): void {
    // For leaflet to render selected layer after navigation
    // we need to make sure the selected layer is added to map and re-added after that
    this.setMapLayers(this.selectedLayers, []);
    this.setMapLayers(this.selectedLayers, this.selectedLayers);
  }

  get shapes(): Observable<ShapeAdded> {
    return this.shapes$.asObservable();
  }

  private drawGeometryToLayer(geometryCollection: GeoJSON.GeometryCollection,
                              drawLayer: L.LayerGroup,
                              style?: GeoJSONOptions, featureInfo?: MapFeatureInfo) {
    if (geometryCollection.geometries.length) {
      const featureCollection = this.mapUtil.createFeatureCollection(geometryCollection, featureInfo);
      this.drawFeaturesToLayer(featureCollection, drawLayer, style);
    }
  }

  private drawFeaturesToLayer(featureCollection: FeatureCollection<GeometryObject>,
                              drawLayer: L.LayerGroup,
                              style?: GeoJSONOptions): void {
    style = style || {};
    style.pointToLayer = (point, latlng) => L.marker(latlng, {icon: alluIcon})
      .bindPopup((layer: any) => this.popupService.create([layer.feature]), {className: 'allu-map-popup'});
    const geoJSON = L.geoJSON(featureCollection, style);
    this.drawGeoJSON(geoJSON, drawLayer);
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
      maxZoom: 12,
      maxBounds:
        L.latLngBounds(L.latLng(59.9084989595170114, 24.4555930248625906), L.latLng(60.4122137731072542, 25.2903558783246289)),
      crs: this.projection.EPSG3879,
      continuousWorld: true,
      worldCopyJump: false
    };
    return L.map('map', mapOption);
  }

  private setupEventHandling(editedItems: L.FeatureGroup): void {
    const self = this;
    this.map.on('draw:created', (e: any) => {
      if (this.mapUtil.isValidGeometry(e.layer)) {
        editedItems.addLayer(e.layer);
        self.shapes$.next(new ShapeAdded(editedItems));
        e.layer.showMeasurements(translations.map.measure);
      } else {
        this.map.removeLayer(e.layer);
      }
    });

    this.map.on('draw:edited', (e: any) => {
      this.removeInvalidLayers(e.layers);
      self.shapes$.next(new ShapeAdded(editedItems));
    });

    this.map.on('draw:deleted', (e: any) => self.shapes$.next(new ShapeAdded(editedItems)));

    this.map.on('draw:drawstart draw:editstart', () => {
      self.editing = true;
      self.editedItems.bringToFront();
    });

    this.map.on('draw:drawstop draw:editstop', () => self.editing = false);

    this.map.on('draw:deletestart', () => {
      self.deleting = true;
      self.editedItems.bringToFront();
    });

    this.map.on('draw:deletestop', () => self.deleting = false);

    this.map.on('moveend', (e: any) => {
      if (!self.config.showOnlyApplicationArea) {
        self.mapStore.mapViewChange(this.map.getBounds(), this.map.getZoom());
      }
    });

    this.map.on(L.Draw.Event.INTERSECTS, (e: any) => {
      this.notification.error(translations.map.areasIntersect, undefined, false);
    });

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      if (!(this.editing || this.deleting)) {
        self.showTooltipOnClick(e);
      }
    });

    this.map.on('draw:editvertex ', (e: any) => {
      if (e.poly.intersects()) {
        this.mapStore.invalidGeometryChange(true);
        this.notification.error(translations.map.areaIntersects, undefined, false);
      } else {
        this.mapStore.invalidGeometryChange(false);
      }
    });

    this.mapLayerService.cityDistricts.on('load', (e: any) => this.addCityDistrictLabels(e.layers));
  }

  private setupLayerControls(): void {
    // Add marker group support for application layers
    this.mapLayerService.markerSupport.addTo(this.map);
    this.mapLayerService.markerSupport.checkIn(this.mapLayerService.contentLayerArray);
    this.focusedItems = L.featureGroup();
    this.focusedItems.addTo(this.map);
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
      L.popup({className: 'allu-map-popup'})
        .setLatLng(e.latlng)
        .setContent(this.popupService.create(intersecting))
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
      const noSelectedSections = !sections || sections.length === 0;
      return drawing && noSelectedSections;
    };

    combineLatest([
      this.mapStore.drawingAllowed,
      this.mapStore.fixedLocations
    ]).pipe(
      map(([drawing, sections]) => drawingAllowed(drawing, sections)),
      takeUntil(this.destroy),
      distinctUntilChanged(),
    ).subscribe(allowed => this.setDynamicControls(allowed));
  }

  private removeInvalidLayers(layers: L.LayerGroup): number {
    let removed = 0;
    layers.eachLayer(l => {
      if (!this.mapUtil.isValidGeometry(l)) {
        this.editedItems.removeLayer(l);
        removed++;
      }
    });
    return removed;
  }

  private selectLayers(allLayers: MapLayer[], selectedLayers: MapLayer[]) {
    const selectedIds = selectedLayers.map(s => s.id);
    const deselectLayers = allLayers.filter(layer => selectedIds.indexOf(layer.id) < 0);
    this.setMapLayers(selectedLayers, deselectLayers);
  }

  private setMapLayers(selected: MapLayer[], deselected: MapLayer[]): void {
    deselected.map(mapLayer => mapLayer.layer).forEach(layer => this.map.removeLayer(layer));
    selected.map(mapLayer => mapLayer.layer).forEach(layer => this.map.addLayer(layer));
  }
}
