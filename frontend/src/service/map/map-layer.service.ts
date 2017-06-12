import {ApplicationType} from '../../model/application/type/application-type';
import {EnumUtil} from '../../util/enum.util';
import {findTranslation} from '../../util/translations';

const OVERLAYS: L.Control.LayersObject = {
  kaupunkikartta: L.tileLayer.wms('/wms?',
    {layers: 'helsinki_karttasarja', format: 'image/png', transparent: true}),
  ortoilmakuva: L.tileLayer.wms('/wms?',
    {layers: 'helsinki_ortoilmakuva', format: 'image/png', transparent: true}),
  kiinteistokartta: L.tileLayer.wms('/wms?',
    {layers: 'helsinki_kiinteistokartta', format: 'image/png', transparent: true}),
  ajantasaasemakaava: L.tileLayer.wms('/wms?',
    {layers: 'helsinki_ajantasaasemakaava', format: 'image/png', transparent: true}),
  opaskartta: L.tileLayer.wms('/wms?',
    {layers: 'helsinki_opaskartta', format: 'image/png', transparent: true}),
  kaupunginosajako: L.tileLayer.wms('/wms?',
    {layers: 'helsinki_kaupunginosajako', format: 'image/png', transparent: true})
  // working URL for accessing Helsinki maps directly (requires authentication)
  // testi: new L.tileLayer.wms('http://kartta.hel.fi/ws/geoserver/helsinki/wms?helsinki',
  //   {layers: 'helsinki:Kaupunkikartta'}),
  // TMS works, but unfortunately seems to use somehow invalid CRS. Thus, WMS is used. Left here for possible future use
  // testi: new L.TileLayer('http://10.176.127.67:8080/tms/1.0.0/helsinki_kaupunkikartta/EPSG_3879/{z}/{x}/{y}.png',
  //   { tms: true }),
};

export class MapLayerService {
  _applicationLayers: {[key: string]: L.FeatureGroup} = {};

  constructor() {
    EnumUtil.enumValues(ApplicationType)
      .map(type => findTranslation(['application.type', type]))
      .forEach(type => this._applicationLayers[type] = L.featureGroup());
  }

  get overlays(): L.Control.LayersObject {
    return OVERLAYS;
  }

  get applicationLayers(): {[key: string]: L.FeatureGroup} {
    return this._applicationLayers;
  }

  get applicationLayerArray(): Array<L.FeatureGroup> {
    return Object.keys(this._applicationLayers).map(k => this._applicationLayers[k]);
  }
}
