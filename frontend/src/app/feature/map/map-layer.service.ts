import {ApplicationType} from '@model/application/type/application-type';
import {findTranslation} from '@util/translations';
import {AuthService} from '@service/authorization/auth.service';
import {Injectable} from '@angular/core';
import {ConfigService} from '@service/config/config.service';
import {FeatureGroupsObject} from '../../model/map/feature-groups-object';
import {CITY_DISTRICTS, pathStyle, winkki} from '../../service/map/map-draw-styles';
import {MapUtil} from '@service/map/map.util';
import * as L from 'leaflet';
import {PathOptions} from 'leaflet';
import 'leaflet.markercluster';
import 'leaflet.markercluster.layersupport';
import '../../js/leaflet/tilelayer-auth';
import '../../js/leaflet/wfs-geojson';
import 'leaflet-wfst';
import {Observable} from 'rxjs';
import {map} from 'rxjs/internal/operators';
import TimeoutOptions = L.TimeoutOptions;

const timeout: TimeoutOptions = {
  response: 60000, // Wait max x seconds for the server to start sending,
  deadline: 60000 // but allow y seconds for the file to finish loading.
};

export const DEFAULT_OVERLAY = 'helsinki_karttasarja';
const STATUS_PLAN = 'PLAN';
const STATUS_ACTIVE = 'ACTIVE';
const DETAILED_LAYER_MIN_ZOOM = 10;
const OVERLAY_TILE_SIZE = 512; // px

export const commonLayers = {
  'Karttasarja': null,
  'Kantakartta': null,
  'Ajantasa-asemakaava': null,
  'Kiinteistökartta': null,
  'Ortoilmakuva': null,
  'Winkin kartat': {
    'Tulevat katutyöt': null,
    'Aktiiviset katutyöt': null,
    'Tulevat vuokraukset': null,
    'Aktiiviset vuokraukset': null,
  },
  'Muut': {
    'Kaupunginosat': null
  }
};

export const restrictedLayers = {
  'Maalämpökaivot': null,
  'Kiinteistökartat': {
    'Maanomistus ja vuokraus yhdistelmä': null,
    'Maanomistus vuokrausalueet': null,
    'Maanomistus sisäinen vuokraus': null
  },
  'Maanalaiset tilat': {
    'Maanalaiset tilat reunaviivat': null,
    'Maanalaiset tilat alueet': null,
  },
  'Johtokartat': {
    'Sähkö': null,
    'Tietoliikenne': null,
    'Kaukolampo': null,
    'Kaasu': null,
    'Vesijohto': null,
    'Viemari': null,
    'Yhdistelmäjohtokartta': null
  },
};

export const applicationLayers = Object.keys(ApplicationType)
  .map(type => findTranslation(['application.type', type]));

@Injectable()
export class MapLayerService {
  public readonly contentLayers: FeatureGroupsObject;
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

  constructor(private authService: AuthService, private config: ConfigService, private mapUtil: MapUtil) {
    const contentLayers = {};
    applicationLayers.forEach(type => contentLayers[type] = L.featureGroup());
    this.contentLayers = contentLayers;

    this.winkkiRoadWorks = {
      'Tulevat katutyöt': this.createWinkkiLayer('winkki_works', STATUS_PLAN, winkki.ROAD_WORKS),
      'Aktiiviset katutyöt': this.createWinkkiLayer('winkki_works', STATUS_ACTIVE, winkki.ROAD_WORKS)
    };

    this.winkkiEvents = {
      'Tulevat vuokraukset': this.createWinkkiLayer('winkki_rents_audiences', STATUS_PLAN, winkki.EVENT),
      'Aktiiviset vuokraukset': this.createWinkkiLayer('winkki_rents_audiences', STATUS_ACTIVE, winkki.EVENT)
    };

    this.cityDistricts = this.createCityDistrictLayer();
    this.other = {
      'Kaupunginosat': this.cityDistricts
    };

    this.clickableLayers = []
      .concat(this.toArray(this.contentLayers))
      .concat(this.toArray(this.winkkiRoadWorks))
      .concat(this.toArray(this.winkkiEvents));
  }

  get contentLayerArray(): Array<L.FeatureGroup> {
    return Object.keys(this.contentLayers).map(k => this.contentLayers[k]);
  }

  createLayerTreeStructure(): Observable<any> {
    return this.config.isStagingOrProduction().pipe(
      map(stagingOrProduction => stagingOrProduction
          ? this.createLayerTreeWithRestricted()
          : this.createCommonLayerTree()
      )
    );
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
      'Maanalaiset tilat reunaviivat': this.createAuthenticatedOverlayLayer('helsinki_maanalaiset_tilat', token, DETAILED_LAYER_MIN_ZOOM),
      'Maanalaiset tilat alueet': this.createAuthenticatedOverlayLayer('helsinki_maanalaiset_tilat_alueet', token, DETAILED_LAYER_MIN_ZOOM),
      'Maalämpökaivot': this.createAuthenticatedOverlayLayer('helsinki_maalampokaivot', token),
      'Sähkö': this.createAuthenticatedOverlayLayer('helsinki_johtokartta_sahko', token, DETAILED_LAYER_MIN_ZOOM),
      'Tietoliikenne': this.createAuthenticatedOverlayLayer('helsinki_johtokartta_tietoliikenne', token, DETAILED_LAYER_MIN_ZOOM),
      'Kaukolampo': this.createAuthenticatedOverlayLayer('helsinki_johtokartta_kaukolampo', token, DETAILED_LAYER_MIN_ZOOM),
      'Kaasu': this.createAuthenticatedOverlayLayer('helsinki_johtokartta_kaasu', token, DETAILED_LAYER_MIN_ZOOM),
      'Vesijohto': this.createAuthenticatedOverlayLayer('helsinki_johtokartta_vesijohto', token, DETAILED_LAYER_MIN_ZOOM),
      'Viemari': this.createAuthenticatedOverlayLayer('helsinki_johtokartta_viemari', token, DETAILED_LAYER_MIN_ZOOM),
      'Yhdistelmäjohtokartta': this.createAuthenticatedOverlayLayer('helsinki_johtokartta_yhdistelma', token, DETAILED_LAYER_MIN_ZOOM),
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
      url: '/geoserver/hkr/ows',
      typeNS: 'hkr',
      typeName: layerName,
      geometryField: 'wkb_geometry',
      crs: this.mapUtil.EPSG3879,
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
      crs: this.mapUtil.EPSG3879,
      style: CITY_DISTRICTS
    });
  }

  private toArray(layers: L.Control.LayersObject): L.Layer[] {
    return Object.keys(layers).map(key => layers[key]);
  }

  private createCommonLayerTree() {
    return {
      'Karttatasot': commonLayers,
      'Hakemustyypit': this.createApplicationlayerTree()
    };
  }

  private createLayerTreeWithRestricted() {
    return {
      'Karttatasot': {
        ...commonLayers,
        ...restrictedLayers
      },
      'Hakemustyypit': this.createApplicationlayerTree()
    };
  }

  private createApplicationlayerTree() {
    return applicationLayers.reduce((tree, layer) => {
      tree[layer] = null;
      return tree;
    }, {});
  }

  private createAuthenticatedOverlayLayer(layerName: string, token: string, minZoom?: number): L.TileLayer {
    const url = `/tms/1.0.0/${layerName}/EPSG_3879/{z}/{x}/{-y}.png`;
    return L.tileLayer.auth(url, {
      format: 'image/png',
      minZoom,
      token,
      timeout: timeout
    });
  }

  private createOverlayLayer(layerName: string, minZoom?: number): L.TileLayer {
    const url = `/tms/1.0.0/${layerName}/EPSG_3879/{z}/{x}/{-y}.png`;
    return L.tileLayer(url, {
      format: 'image/png',
      minZoom
    });
  }
}
