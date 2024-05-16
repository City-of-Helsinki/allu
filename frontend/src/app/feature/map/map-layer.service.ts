import {ApplicationType, applicationTypeList} from '@model/application/type/application-type';
import {findTranslation} from '@util/translations';
import {AuthService} from '@service/authorization/auth.service';
import {Injectable} from '@angular/core';
import {CITY_DISTRICTS, pathStyle, winkki} from '../../service/map/map-draw-styles';
import {Projection} from '@feature/map/projection';
import * as L from 'leaflet';
import {PathOptions} from 'leaflet';
import 'leaflet.markercluster';
import 'leaflet.markercluster.layersupport';
import '../../js/leaflet/tilelayer-auth';
import '../../js/leaflet/wfs-geojson';
import 'leaflet-wfst';
import TimeoutOptions = L.TimeoutOptions;
import {MapLayer} from '@service/map/map-layer';
import {ArrayUtil} from '@util/array-util';
import {FeatureGroup} from 'leaflet';
import {Some} from '@util/option';
import {ZoomLevel} from '@feature/map/zoom-level';

const timeout: TimeoutOptions = {
  response: 60000, // Wait max x seconds for the server to start sending,
  deadline: 60000 // but allow y seconds for the file to finish loading.
};

export const DEFAULT_OVERLAY = 'helsinki_karttasarja';
const STATUS_PLAN = 'PLAN';
const STATUS_ACTIVE = 'ACTIVE';
const OVERLAY_TILE_SIZE = 512; // px

export const applicationLayers = Object.keys(ApplicationType)
  .map(type => findTranslation(['application.type', type]));

@Injectable()
export class MapLayerService {
  public readonly contentLayers: MapLayer[];
  public readonly winkkiRoadWorks: L.Control.LayersObject;
  public readonly winkkiEvents: L.Control.LayersObject;
  public readonly other: L.Control.LayersObject;
  public readonly cityDistricts: L.FeatureGroup;
  public readonly clickableLayers = [];
  public readonly markerSupport = L.markerClusterGroup.layerSupport({
    spiderfyOnMaxZoom: true,
    showCoverageOnHover: false,
    zoomToBoundsOnClick: true
  });

  constructor(private authService: AuthService, private projection: Projection) {
    this.contentLayers = applicationTypeList
      .map(type => new MapLayer(findTranslation(['application.type', type]), L.featureGroup(), type));

    this.winkkiRoadWorks = {
      'Tulevat katutyöt': this.createWinkkiLayer('Winkki_works', STATUS_PLAN, winkki.ROAD_WORKS),
      'Aktiiviset katutyöt': this.createWinkkiLayer('Winkki_works', STATUS_ACTIVE, winkki.ROAD_WORKS)
    };

    this.winkkiEvents = {
      'Tulevat vuokraukset': this.createWinkkiLayer('Winkki_rents_audiences', STATUS_PLAN, winkki.EVENT),
      'Aktiiviset vuokraukset': this.createWinkkiLayer('Winkki_rents_audiences', STATUS_ACTIVE, winkki.EVENT)
    };

    this.cityDistricts = this.createCityDistrictLayer();
    this.other = {
      'Kaupunginosat': this.cityDistricts
    };

    this.clickableLayers = []
      .concat(Object.keys(this.contentLayers).map(key => this.contentLayers[key].layer))
      .concat(this.toArray(this.winkkiRoadWorks))
      .concat(this.toArray(this.winkkiEvents));
  }

  getContentLayer(layerName: string): L.FeatureGroup {
    return Some(ArrayUtil.first(this.contentLayers, (l => l.id === layerName)))
      .map(mapLayer => <FeatureGroup>mapLayer.layer)
      .orElse(undefined);
  }

  get contentLayerArray(): L.FeatureGroup[] {
    return this.contentLayers
      .map(cl => cl.layer)
      .map(layer => <L.FeatureGroup>layer);
  }

  createOverlays(): L.Control.LayersObject {
    const token = this.authService.token;
    return {
      'Karttasarja': this.createOverlayLayer('helsinki_karttasarja'),
      'Kantakartta': this.createOverlayLayer('helsinki_kantakartta'),
      'Ajantasa-asemakaava': this.createOverlayLayer('helsinki_ajantasa_asemakaava'),
      'Kiinteistökartta': this.createOverlayLayer('helsinki_kiinteistokartta'),
      'Ortoilmakuva': this.createOverlayLayer('helsinki_ortoilmakuva')
    };
  }

  createRestrictedOverlays(): L.Control.LayersObject {
    const token = this.authService.token;
    return {
      'Maanomistus ja vuokraus yhdistelmä': this.createAuthenticatedOverlayLayer('helsinki_maanomistus_vuokrausalueet_yhdistelma', token),
      'Maanomistus vuokrausalueet': this.createAuthenticatedOverlayLayer('helsinki_maanomistus_vuokrausalueet', token),
      'Maanomistus sisäinen vuokraus': this.createAuthenticatedOverlayLayer('helsinki_maanomistus_sisainen', token),
      'Maanalaiset tilat reunaviivat': this.createAuthenticatedOverlayLayer('helsinki_maanalaiset_tilat', token, ZoomLevel.METERS_50),
      'Maanalaiset tilat alueet': this.createAuthenticatedOverlayLayer('helsinki_maanalaiset_tilat_alueet', token, ZoomLevel.METERS_50),
      'Maalämpökaivot': this.createAuthenticatedOverlayLayer('helsinki_maalampokaivot', token),
      'Porakaivot': this.createAuthenticatedOverlayLayer('helsinki_porakaivo_vesi', token),
      'Sähkö': this.createAuthenticatedOverlayLayer('helsinki_johtokartta_sahko', token, ZoomLevel.METERS_10),
      'Tietoliikenne': this.createAuthenticatedOverlayLayer('helsinki_johtokartta_tietoliikenne', token, ZoomLevel.METERS_10),
      'Kaukolämpö': this.createAuthenticatedOverlayLayer('helsinki_johtokartta_kaukolampo', token, ZoomLevel.METERS_10),
      'Kaukojäähdytys': this.createAuthenticatedOverlayLayer('helsinki_johtokartta_kaukojaahdytys', token, ZoomLevel.METERS_10),
      'Kaasu': this.createAuthenticatedOverlayLayer('helsinki_johtokartta_kaasu', token, ZoomLevel.METERS_10),
      'Vesijohto': this.createAuthenticatedOverlayLayer('helsinki_johtokartta_vesi', token, ZoomLevel.METERS_10),
      'Viemari': this.createAuthenticatedOverlayLayer('helsinki_johtokartta_viemari', token, ZoomLevel.METERS_10),
      'Imujätehuolto': this.createAuthenticatedOverlayLayer('helsinki_johtokartta_imujatehuolto', token, ZoomLevel.METERS_10),
      'Yhdistelmäjohtokartta': this.createAuthenticatedOverlayLayer('helsinki_johtokartta_yhdistelma', token, ZoomLevel.METERS_10),
    };
  }

  createOverlay(layerName: string): L.TileLayer {
    return this.createOverlayLayer(layerName);
  }

  private createWinkkiLayer(layerName: string, status: string, style: PathOptions = pathStyle.DEFAULT): L.FeatureGroup {
    const statusFilter = L.Filter.eq('licence_status', status);
    return this.winkkiWFS(layerName, statusFilter, style);
  }

  private winkkiWFS(layerName: string, wfsFilter: L.Filter, style: PathOptions): L.FeatureGroup {
    return L.wfs({
      url: 'https://kartta.hel.fi/ws/geoserver/avoindata/wfs',
      typeNS: 'avoindata',
      typeName: layerName,
      geometryField: 'geom',
      crs: this.projection.EPSG3879,
      style: style,
      opacity: style.opacity,
      fillOpacity: style.fillOpacity,
      showExisting: true,
      filter: wfsFilter
    });
  }

  private createCityDistrictLayer(): L.FeatureGroup {
    return L.wfsGeoJSON({
      url: 'https://kartta.hel.fi/ws/geoserver/avoindata/wfs',
      typeName: 'avoindata:Kaupunginosajako',
      crs: this.projection.EPSG3879,
      style: CITY_DISTRICTS
    });
  }

  private toArray(layers: L.Control.LayersObject): L.Layer[] {
    return Object.keys(layers).map(key => layers[key]);
  }

  private createAuthenticatedOverlayLayer(layerName: string, token: string, minZoom?: number): L.TileLayer {
    const url = `/tms/1.0.0/${layerName}/EPSG_3879/{z}/{x}/{-y}.png`;
    return L.tileLayer.auth(url, {
      minZoom,
      token,
      timeout: timeout
    });
  }

  private createOverlayLayer(layerName: string, minZoom?: number): L.TileLayer {
    const url = `/tms/1.0.0/${layerName}/EPSG_3879/{z}/{x}/{-y}.png`;
    return L.tileLayer(url, {
      minZoom
    });
  }
}
