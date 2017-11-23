import {Geocoordinates} from '../../model/common/geocoordinates';
import {BackendGeocoordinates} from '../backend-model/backend-geocoordinates';
import {MapUtil} from '../map/map.util';


export class GeocoordinatesMapper {

  public static mapBackend(backendGeocoordinates: BackendGeocoordinates, mapService: MapUtil): Geocoordinates {
    const coordinates = mapService.epsg3879ToWgs84([backendGeocoordinates.x, backendGeocoordinates.y]);
    return Geocoordinates.fromArray(coordinates);
  }

  public static mapFrontend(geocoordinates: Geocoordinates, mapService: MapUtil): BackendGeocoordinates {
    if (geocoordinates) {
      const coordinates = mapService.wgs84ToEpsg3879(geocoordinates.toArray());
      return {
        x: coordinates[0],
        y: coordinates[1]
      };
    } else {
      return undefined;
    }
  }
}
