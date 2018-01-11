import {ApplicationType} from '../../model/application/type/application-type';
import {EnumUtil} from '../../util/enum.util';
import {findTranslation} from '../../util/translations';
import '../../js/leaflet/wms-authentication';
import {AuthService} from '../authorization/auth.service';
import {Injectable} from '@angular/core';
import TimeoutOptions = L.TimeoutOptions;
import {ConfigService} from '../config/config.service';

const timeout: TimeoutOptions = {
  response: 10000,
  deadline: 60000
};

export const DEFAULT_OVERLAY = 'Karttasarja';

@Injectable()
export class MapLayerService {
  private _applicationLayers: {[key: string]: L.FeatureGroup} = {};
  private _overlays: L.Control.LayersObject = {};

  constructor(private authService: AuthService, private config: ConfigService) {
    this.initOverlays();

    EnumUtil.enumValues(ApplicationType)
      .map(type => findTranslation(['application.type', type]))
      .forEach(type => this._applicationLayers[type] = L.featureGroup());
  }

  get overlays(): L.Control.LayersObject {
    return this._overlays;
  }

  get applicationLayers(): {[key: string]: L.FeatureGroup} {
    return this._applicationLayers;
  }

  get applicationLayerArray(): Array<L.FeatureGroup> {
    return Object.keys(this._applicationLayers).map(k => this._applicationLayers[k]);
  }

  private initOverlays(): void {
    const token = this.authService.token;
    this._overlays = {
      'Karttasarja': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_karttasarja', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      'Kantakartta': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_kantakartta', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      'Ajantasa-asemakaava': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_ajantasa_asemakaava', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      'Kiinteistökartta': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_kiinteistokartta', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      'Ortoilmakuva': L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_ortoilmakuva', format: 'image/png', transparent: true, token: token, timeout: timeout})
    };

    this.config.isProduction()
      .filter(isProd => isProd)
      .subscribe(isProd => {
        this._overlays = {...this._overlays, ...this.initRestrictedOverlays(token) };
    });
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
}
