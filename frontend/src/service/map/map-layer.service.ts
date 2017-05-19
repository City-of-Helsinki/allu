import {ApplicationType} from '../../model/application/type/application-type';
import {EnumUtil} from '../../util/enum.util';
import {findTranslation} from '../../util/translations';

const OVERLAYS: L.Control.LayersObject = {
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

export class MapLayerService {
  get overlays(): L.Control.LayersObject {
    return OVERLAYS;
  }

  get applicationLayers(): {[key: string]: L.FeatureGroup} {
    let applicationLayers: {[key: string]: L.FeatureGroup} = {};
    EnumUtil.enumValues(ApplicationType)
      .map(type => findTranslation(['application.type', type]))
      .forEach(type => applicationLayers[type] = L.featureGroup());
    return applicationLayers;
  }

}
