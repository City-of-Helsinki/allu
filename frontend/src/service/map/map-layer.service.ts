import {ApplicationType} from '../../model/application/type/application-type';
import {EnumUtil} from '../../util/enum.util';
import {findTranslation} from '../../util/translations';
import '../../js/leaflet/wms-authentication';
import {AuthService} from '../authorization/auth.service';
import {Injectable} from '@angular/core';
import TimeoutOptions = L.TimeoutOptions;

const timeout: TimeoutOptions = {
  response: 10000,
  deadline: 60000
};

@Injectable()
export class MapLayerService {
  _applicationLayers: {[key: string]: L.FeatureGroup} = {};
  _overlays: L.Control.LayersObject = {};

  constructor(private authService: AuthService) {
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
      kaupunkikartta: L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_karttasarja', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      ortoilmakuva: L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_ortoilmakuva', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      kiinteistokartta: L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_kiinteistokartta', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      ajantasaasemakaava: L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_ajantasaasemakaava', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      opaskartta: L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_opaskartta', format: 'image/png', transparent: true, token: token, timeout: timeout}),
      kaupunginosajako: L.tileLayer.wmsAuth('/wms?',
        {layers: 'helsinki_kaupunginosajako', format: 'image/png', transparent: true, token: token, timeout: timeout})
      // working URL for accessing Helsinki maps directly (requires authentication)
      // testi: new L.tileLayer.wms('http://kartta.hel.fi/ws/geoserver/helsinki/wms?helsinki',
      //   {layers: 'helsinki:Kaupunkikartta'}),
      // TMS works, but unfortunately seems to use somehow invalid CRS. Thus, WMS is used. Left here for possible future use
      // testi: new L.TileLayer('http://10.176.127.67:8080/tms/1.0.0/helsinki_kaupunkikartta/EPSG_3879/{z}/{x}/{y}.png',
      //   { tms: true }),
    };
  }
}
