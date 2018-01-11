import 'leaflet';
import 'leaflet-draw';
import 'proj4leaflet';
import 'leaflet-groupedlayercontrol';
import 'leaflet-measure-path';

import {Subject} from 'rxjs/Subject';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';

import {MapUtil} from './map.util';
import {Some} from '../../util/option';
import {translations} from '../../util/translations';
import {Geocoordinates} from '../../model/common/geocoordinates';
import {pathStyle} from './map-draw-styles';
import {MapPopup} from './map-popup';
import {DEFAULT_OVERLAY, MapLayerService} from './map-layer.service';
import '../../js/leaflet/draw-transform';
import '../../js/leaflet/draw-intersect';
import '../../js/leaflet/draw-line';
import {NotificationService} from '../notification/notification.service';
import GeoJSONOptions = L.GeoJSONOptions;

const alluIcon = L.icon({
  iconUrl: 'assets/images/marker-icon.png',
  shadowUrl: undefined
});

export class ShapeAdded {
  constructor(public features: L.FeatureGroup, public affectsControls: boolean = true) {}
}

export interface MapStateConfig {
  draw: boolean;
  edit: boolean;
  zoom: boolean;
  selection: boolean;
  showOnlyApplicationArea;
}

export class MapState {
  private map: L.Map;
  private defaultOverlay: L.Layer;
  private mapOverlayLayers: any;
  private drawControl: L.Control.Draw;
  private drawnItems: {[key: string]: L.FeatureGroup} = {};
  private focusedItems: L.FeatureGroup;
  private editedItems: L.FeatureGroup;
  private shapes$ = new Subject<ShapeAdded>();
  private mapView$: BehaviorSubject<GeoJSON.GeometryObject>;

  constructor(
    private mapUtil: MapUtil,
    private mapLayerService: MapLayerService,
    private config: MapStateConfig
  ) {
    this.initMap();
  }

  public clearDrawn() {
    Object.keys(this.drawnItems)
      .map(key => this.drawnItems[key])
      .map(featureGroup => featureGroup.clearLayers());
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

    const draw = controlsEnabled ? {
      // todo: this <false>false can be removed when typescript compiler allows type parameter of | false
      polyline: <false>false,
      marker: <false>false,
      circlemarker: <false>false,
      polygon: {
        shapeOptions: pathStyle.DEFAULT_DRAW,
        allowIntersection: false,
        showArea: true
      },
      circle: {
        shapeOptions: pathStyle.DEFAULT_DRAW
      },
      rectangle: {
        shapeOptions: pathStyle.DEFAULT_DRAW
      },
      bufferPolyline: {
        shapeOptions: pathStyle.DEFAULT_DRAW,
        polyOptions: {
          shapeOptions: pathStyle.DEFAULT_DRAW
        }
      }
    } : undefined;

    const edit = controlsEnabled ? { selectedPathOptions: pathStyle.DEFAULT_EDIT } : false;

    const drawControl = new L.Control.Draw({
      position: 'topright',
      draw: draw,
      intersectLayers: this.mapLayerService.applicationLayerArray,
      edit: {
        featureGroup: items,
        edit: edit,
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
                      style?: Object, popup?: MapPopup) {
    const layer = this.drawnItems[layerName];
    if (layer) {
      geometries.forEach(g => this.drawGeometryToLayer(g, layer, style, popup));
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
    this.drawGeometryToLayer(geometry, this.editedItems, style);
    this.showMeasurements(this.editedItems);
  }

  public fitEditedToView() {
    Some(this.editedItems.getBounds())
      .filter(bounds => Object.keys(bounds).length > 0) // has some bounds
      .do(bounds => this.map.fitBounds(bounds));
  }

  // For some reason leaflet does not show layer after angular route change
  // unless it is removed and added back
  selectDefaultLayer(): void {
    this.map.removeLayer(this.defaultOverlay);
    this.map.addLayer(this.defaultOverlay);
  }

  get shapes(): Observable<ShapeAdded> {
    return this.shapes$.asObservable();
  }

  get mapView(): Observable<GeoJSON.GeometryObject> {
    return this.mapView$.asObservable();
  }

  private drawGeometryToLayer(geometryCollection: GeoJSON.GeometryCollection,
                              drawLayer: L.LayerGroup,
                              style?: GeoJSONOptions, popup?: MapPopup) {
    if (geometryCollection.geometries.length) {
      style = style || {};
      const featureCollection = this.mapUtil.geometryCollectionToFeatureCollection(geometryCollection);
      style.pointToLayer = (point, latlng) => L.marker(latlng, {icon: alluIcon});
      const geoJSON = L.geoJSON(featureCollection, style);
      this.drawGeoJSON(geoJSON, drawLayer, popup);
    }
  }

  private drawGeoJSON(geoJSON: L.GeoJSON, drawLayer: L.LayerGroup, popup?: MapPopup): void {
    geoJSON.eachLayer((l: any) => {
      Some(popup).do(pu => l.bindPopup((_) => pu.content()));
      drawLayer.addLayer(l);
    });
  }

  private initMap(): void {
    this.map = this.createMap();
    this.mapView$ = new BehaviorSubject(this.getCurrentMapView());

    const editedItems = L.featureGroup();
    editedItems.addTo(this.map);
    this.editedItems = editedItems;

    this.setupEventHandling(editedItems);
    this.setupLayers();

    L.control.zoom({
      position: 'topright',
      zoomInTitle: translations.map.zoomIn,
      zoomOutTitle: translations.map.zoomOut
    }).addTo(this.map);
    L.control.scale().addTo(this.map);
    L.Icon.Default['imagePath'] = '/assets/images/';
    this.setDynamicControls(true, editedItems);
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
      layers: this.createLayers(),
      crs: this.mapUtil.getEPSG3879(),
      continuousWorld: true,
      worldCopyJump: false
    };
    return L.map('map', mapOption);
  }

  private createLayers(): Array<L.Layer> {
    // Default selected layers and overlays
    this.mapOverlayLayers = this.mapLayerService.overlays;
    this.defaultOverlay = this.mapOverlayLayers[DEFAULT_OVERLAY];
    this.drawnItems = this.mapLayerService.applicationLayers;
    return [this.defaultOverlay].concat(this.mapLayerService.applicationLayerArray);
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

    this.map.on('moveend', (e: any) => {
      if (!self.config.showOnlyApplicationArea) {
        self.mapView$.next(self.getCurrentMapView());
      }
    });

    this.map.on(L.Draw.Event.INTERSECTS, (e: any) => {
      NotificationService.errorMessage(translations.map.areasIntersect, 2000);
    });
  }

  private setupLayers(): void {
    const groupedOverlays = {
      Karttatasot: this.mapOverlayLayers,
      Hakemustyypit: this.drawnItems
    };
    this.focusedItems = L.featureGroup();

    L.control.groupedLayers(undefined, groupedOverlays).addTo(this.map);
    this.focusedItems.addTo(this.map);
  }

  private getCurrentMapView(): GeoJSON.GeometryObject {
    const viewPoly = this.mapUtil.polygonFromBounds(this.map.getBounds());
    return this.mapUtil.featureToGeometry(viewPoly.toGeoJSON());
  }

  private setLocalizations(): void {
    L.drawLocal = translations.map;
  }

  private drawLayers(): L.FeatureGroup {
    return this.mapLayerService.applicationLayerArray
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
}
