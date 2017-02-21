import {Injectable} from '@angular/core';
import 'leaflet';
import 'leaflet-draw';
import 'proj4leaflet';
import {Subject} from 'rxjs/Subject';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';

import {MapUtil} from './map.util';
import {Some} from '../../util/option';
import {translations, findTranslation} from '../../util/translations';
import {EnumUtil} from '../../util/enum.util';
import {ApplicationType} from '../../model/application/type/application-type';
import {Geocoordinates} from '../../model/common/geocoordinates';
import {styleByApplicationType} from './map-draw-styles';
import {MapPopup} from './map-popup';

export class ShapeAdded {
  constructor(public features: L.FeatureGroup, public affectsControls: boolean = true) {}
}

export class MapState {
  private map: L.Map;
  private mapLayers: any;
  private drawControl: L.Control.Draw;
  private drawnItems: {[key: string]: L.FeatureGroup} = {};
  private editedItems: L.FeatureGroup;
  private shapes$ = new Subject<ShapeAdded>();
  private mapView$: BehaviorSubject<GeoJSON.GeometryObject>;

  constructor(
    private mapUtil: MapUtil,
    private draw: boolean = false,
    private edit: boolean = false,
    private zoom: boolean = false,
    private selection: boolean = false,
    private showOnlyApplicationArea: boolean = false
  ) {
    this.mapLayers = this.createBaseLayers();
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
    let items = editedItems || this.editedItems;

    let draw = controlsEnabled ? {
        polygon: {
          shapeOptions: styleByApplicationType.DEFAULT,
          allowIntersection: false,
          showArea: true
        },
        circle: {
          shapeOptions: styleByApplicationType.DEFAULT
        },
        rectangle: {
          shapeOptions: styleByApplicationType.DEFAULT
        },
        polyline: {
          shapeOptions: styleByApplicationType.DEFAULT
        },
        marker: false
      } : false;

    let drawControl = new L.Control.Draw({
      position: 'topright',
      draw: draw,
      edit: {
        featureGroup: items,
        edit: controlsEnabled,
        remove: controlsEnabled
      }
    });
    this.setLocalizations();

    if (this.draw) {
      // remove old control
      Some(this.drawControl).do(control => this.map.removeControl(control));
      this.map.addControl(drawControl);
      this.drawControl = drawControl;
    }
  }

  public drawGeometry(geometryCollection: GeoJSON.GeometryCollection, layerName: string,
                      style?: Object, popup?: MapPopup) {
    let layer = this.drawnItems[layerName];
    if (layer) {
      this.drawGeometryToLayer(geometryCollection, layer, style, popup);
    } else {
      throw new Error('No draw layer with name ' + layerName);
    }
  }

  public drawFixedLocations(geometries: Array<GeoJSON.GeometryCollection>, style?: Object) {
    geometries.forEach(geometry => this.drawEditableGeometry(geometry, style));
    this.shapes$.next(new ShapeAdded(this.editedItems, false));
  }

  public drawEditableGeometry(geometryCollection: GeoJSON.GeometryCollection, style?: Object) {
    this.drawGeometryToLayer(geometryCollection, this.editedItems, style);
  }

  public fitEditedToView() {
    Some(this.editedItems.getBounds())
      .filter(bounds => Object.keys(bounds).length > 0) // has some bounds
      .do(bounds => this.map.fitBounds(bounds));
  }

  get shapes(): Observable<ShapeAdded> {
    return this.shapes$.asObservable();
  }

  get mapView(): Observable<GeoJSON.GeometryObject> {
    return this.mapView$.asObservable();
  }

  private drawGeometryToLayer(geometryCollection: GeoJSON.GeometryCollection,
                              drawLayer: L.LayerGroup,
                              style?: Object, popup?: MapPopup) {
    if (geometryCollection.geometries.length) {
      let featureCollection = this.mapUtil.geometryCollectionToFeatureCollection(geometryCollection);
      let geoJSON = L.geoJSON(featureCollection, style);

      geoJSON.eachLayer((layer) => {
        Some(popup).do(pu => layer.bindPopup((l) => pu.content()));
        drawLayer.addLayer(layer);
      });
    }
  }

  private initMap(): void {
    EnumUtil.enumValues(ApplicationType)
      .map(type => findTranslation(['application.type', type]))
      .forEach(type => this.drawnItems[type] = L.featureGroup());

    this.map = this.createMap();
    this.mapView$ = new BehaviorSubject(this.getCurrentMapView());

    let editedItems = L.featureGroup();
    editedItems.addTo(this.map);

    let self = this;
    this.map.on('draw:created', function (e: any) {
      editedItems.addLayer(e.layer);
      self.shapes$.next(new ShapeAdded(editedItems));
    });

    this.map.on('draw:edited', function (e: any) {
      self.shapes$.next(new ShapeAdded(editedItems));
    });

    this.map.on('draw:deleted', function (e: any) {
      self.shapes$.next(new ShapeAdded(editedItems));
    });

    this.map.on('moveend', (e: any) => {
      if (!self.showOnlyApplicationArea) {
        this.mapView$.next(this.getCurrentMapView());
      }
    });

    this.editedItems = editedItems;
    L.control.layers(this.mapLayers, this.drawnItems).addTo(this.map);

    L.control.zoom({
      position: 'topright',
      zoomInTitle: translations.map.zoomIn,
      zoomOutTitle: translations.map.zoomOut
    }).addTo(this.map);
    L.control.scale().addTo(this.map);
    L.Icon.Default['imagePath'] = '/assets/images/';
    this.setDynamicControls(true, editedItems);
  }

  private createBaseLayers(): any {
    return {
      kaupunkikartta: L.tileLayer.wms('/wms?',
        {layers: 'helsinki_kaupunkikartta', format: 'image/png'}),
      ortoilmakuva: L.tileLayer.wms('/wms?',
        {layers: 'helsinki_ortoilmakuva', format: 'image/png'}),
      kiinteistokartta: L.tileLayer.wms('/wms?',
        {layers: 'helsinki_kiinteistokartta', format: 'image/png'}),
      ajantasaasemakaava: L.tileLayer.wms('/wms?',
        {layers: 'helsinki_ajantasaasemakaava', format: 'image/png'}),
      opaskartta: L.tileLayer.wms('/wms?',
        {layers: 'helsinki_opaskartta', format: 'image/png'}),
      kaupunginosajako: L.tileLayer.wms('/wms?',
        {layers: 'helsinki_kaupunginosajako', format: 'image/png'})
      // working URL for accessing Helsinki maps directly (requires authentication)
      // testi: new L.tileLayer.wms('http://kartta.hel.fi/ws/geoserver/helsinki/wms?helsinki',
      //   {layers: 'helsinki:Kaupunkikartta'}),
      // TMS works, but unfortunately seems to use somehow invalid CRS. Thus, WMS is used. Left here for possible future use
      // testi: new L.TileLayer('http://10.176.127.67:8080/tms/1.0.0/helsinki_kaupunkikartta/EPSG_3879/{z}/{x}/{y}.png',
      //   { tms: true }),
    };
  }

  private createMap(): L.Map {
    // Default selected layers and overlays
    let layers = [this.mapLayers.kaupunkikartta].concat(this.drawLayers().getLayers());
    let mapOption = {
      zoomControl: false,
      center: L.latLng(60.1708763, 24.9424988), // Helsinki railway station
      scrollWheelZoom: this.zoom,
      zoom: 6,
      minZoom: 3,
      maxZoom: 13,
      maxBounds:
        L.latLngBounds(L.latLng(59.9084989595170114, 24.4555930248625906), L.latLng(60.4122137731072542, 25.2903558783246289)),
      layers: layers,
      crs: this.mapUtil.getEPSG3879(),
      continuousWorld: true,
      worldCopyJump: false
    };
    return L.map('map', mapOption);
  }

  private getCurrentMapView(): GeoJSON.GeometryObject {
    let viewPoly = this.mapUtil.polygonFromBounds(this.map.getBounds());
    return this.mapUtil.featureToGeometry(viewPoly.toGeoJSON());
  }

  private setLocalizations(): void {
    L.drawLocal = translations.map;
  }

  private drawLayers(): L.FeatureGroup {
    return Object.keys(this.drawnItems)
      .map(key => this.drawnItems[key])
      .reduce((allLayers, currentLayer) => {
        allLayers.addLayer(currentLayer);
        return allLayers;
      }, L.featureGroup());
  }
}

@Injectable()
export class MapService {

  constructor(private mapUtil: MapUtil) {}

  public create(
    draw?: boolean,
    edit?: boolean,
    zoom?: boolean,
    selection?: boolean,
    showOnlyApplicationArea?: boolean
  ) {
    return new MapState(
      this.mapUtil,
      draw,
      edit,
      zoom,
      selection,
      showOnlyApplicationArea
    );
  }
}
