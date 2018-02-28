import {ApplicationType} from '../../model/application/type/application-type';
import {EnumUtil} from '../../util/enum.util';
import {findTranslation} from '../../util/translations';
import {AuthService} from '../authorization/auth.service';
import {Injectable} from '@angular/core';
import {ConfigService} from '../config/config.service';
import {FeatureGroupsObject} from '../../model/map/feature-groups-object';
import {pathStyle} from './map-draw-styles';
import {MapUtil} from './map.util';
import * as L from 'leaflet';
import 'leaflet.markercluster';
import 'leaflet.markercluster.layersupport';
import '../../js/leaflet/wms-authentication';
import 'leaflet-wfst';
import TimeoutOptions = L.TimeoutOptions;
import {Observable} from 'rxjs/Observable';

const timeout: TimeoutOptions = {
  response: 10000,
  deadline: 60000
};

const DEFAULT_OVERLAY = 'Karttasarja';
const STATUS_PLAN = 'PLAN';
const STATUS_ACTIVE = 'ACTIVE';

@Injectable()
export class MapLayerService {
  public readonly overlays: L.Control.LayersObject;
  public readonly contentLayers: FeatureGroupsObject;
  public readonly defaultOverlay: L.Layer;
  public readonly winkkiRoadWorks: L.Control.LayersObject;
  public readonly winkkiEvents: L.Control.LayersObject;
  public readonly cityDistricts: L.Control.LayersObject;
  public readonly clickableLayers = [];
  public readonly markerSupport = L.markerClusterGroup.layerSupport({
    spiderfyOnMaxZoom: true,
    showCoverageOnHover: false,
    zoomToBoundsOnClick: true
  });

  private readonly token: string;

  constructor(private authService: AuthService, private config: ConfigService, private mapUtil: MapUtil) {
    this.token = this.authService.token;
    this.overlays = this.createOverlays();
    this.defaultOverlay = this.overlays[DEFAULT_OVERLAY];

    const contentLayers = {};
    EnumUtil.enumValues(ApplicationType)
      .map(type => findTranslation(['application.type', type]))
      .forEach(type => contentLayers[type] = L.featureGroup());
    this.contentLayers = contentLayers;

    this.winkkiRoadWorks = {
      'Tulevat': this.createWinkkiLayer('winkki_works', STATUS_PLAN),
      'Aktiiviset': this.createWinkkiLayer('winkki_works', STATUS_ACTIVE)
    };

    this.winkkiEvents = {
      'Tulevat': this.createWinkkiLayer('winkki_rents_audiences', STATUS_PLAN),
      'Aktiiviset': this.createWinkkiLayer('winkki_rents_audiences', STATUS_ACTIVE)
    };

    this.clickableLayers = []
      .concat(Object.values(this.contentLayers))
      .concat(Object.values(this.winkkiRoadWorks))
      .concat(Object.values(this.winkkiEvents));
  }

  get contentLayerArray(): Array<L.FeatureGroup> {
    return Object.keys(this.contentLayers).map(k => this.contentLayers[k]);
  }

  get initialLayers(): Array<L.Layer> {
    const initialLayer = [this.overlays[DEFAULT_OVERLAY]];
    return initialLayer
      .concat(this.contentLayerArray);
  }

  get restrictedOverlays(): Observable<L.Control.LayersObject> {
    return this.config.isProduction()
      .filter(isProd => isProd)
      .map(() => this.initRestrictedOverlays(this.token));
  }

  private createOverlays(): L.Control.LayersObject {
    return {
      'Karttasarja': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_karttasarja', format: 'image/png', transparent: true, token: this.token, timeout: timeout}),
      'Kantakartta': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_kantakartta', format: 'image/png', transparent: true, token: this.token, timeout: timeout}),
      'Ajantasa-asemakaava': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_ajantasa_asemakaava', format: 'image/png', transparent: true, token: this.token, timeout: timeout}),
      'Kiinteistökartta': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_kiinteistokartta', format: 'image/png', transparent: true, token: this.token, timeout: timeout}),
      'Ortoilmakuva': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_ortoilmakuva', format: 'image/png', transparent: true, token: this.token, timeout: timeout})
    };
  }

  private initRestrictedOverlays(token: string): L.Control.LayersObject {
    return {
      'Maanomistus ja vuokraus yhdistelmä': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_maanomistus_vuokrausalueet_yhdistelma', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      'Maanomistus vuokrausalueet': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_maanomistus_vuokrausalueet', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      'Maanomistus sisäinen vuokraus': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_maanomistus_sisainen', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      'Maanalaiset tilat': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_maanalaiset_tilat', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      'Maanalaiset tilat alueet': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_maanalaiset_tilat_alueet', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      'Maalämpökaivot': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_maalampokaivot', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      'Sähkö': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_johtokartta_sahko', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      'Tietoliikenne': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_johtokartta_tietoliikenne', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      'Kaukolampo': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_johtokartta_kaukolampo', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      'Kaasu': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_johtokartta_kaasu', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      'Vesijohto': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_johtokartta_vesijohto', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      'Viemari': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_johtokartta_viemari', format: 'image/png', transparent: true, token: token, timeout: timeout})
    };
  }

  private createWinkkiLayer(layerName: string, status: string): L.FeatureGroup {
    const statusFilter = L.Filter.eq('licence_status', status);
    return this.winkkiWFS(layerName, statusFilter);
  }

  private winkkiWFS(layerName: string, filter: L.Filter): L.FeatureGroup {
    return L.wfs({
      url: '/geoserver/hkr/ows',
      typeNS: 'hkr',
      typeName: layerName,
      geometryField: 'wkb_geometry',
      crs: this.mapUtil.EPSG3879,
      style: pathStyle.DEFAULT,
      opacity: pathStyle.DEFAULT.opacity,
      fillOpacity: pathStyle.DEFAULT.fillOpacity,
      showExisting: true,
      filter: filter
    });
  }

  private createCityDistrictLayer(): L.FeatureGroup {
    return L.wfs({
      url: 'https://kartta.hel.fi/ws/geoserver/avoindata/wfs',
      typeNS: 'avoindata',
      typeName: 'Kaupunginosajako',
      geometryField: 'geom',
      crs: this.mapUtil.EPSG3879,
      style: pathStyle.DEFAULT,
      opacity: pathStyle.DEFAULT.opacity,
      fillOpacity: pathStyle.DEFAULT.fillOpacity,
      showExisting: true
    });
  }
}
