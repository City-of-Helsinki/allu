import {Geocoordinates} from '../../model/common/geocoordinates';
import {BackendGeocoordinates} from '../backend-model/backend-geocoordinates';
import {MapService} from '../map.service';


export class GeocoordinatesMapper {

  public static mapBackend(backendGeocoordinates: BackendGeocoordinates, mapService: MapService): Geocoordinates {
    let coordinates = mapService.epsg3879ToWgs84([backendGeocoordinates.x, backendGeocoordinates.y]);
    return Geocoordinates.fromArray(coordinates);
  }

  public static mapFrontend(geocoordinates: Geocoordinates, mapService: MapService): BackendGeocoordinates {
    if (geocoordinates) {
      let coordinates = mapService.wgs84ToEpsg3879(geocoordinates.toArray());
      return {
        x: coordinates[0],
        y: coordinates[1]
      };
    } else {
      return undefined;
    }
  }
}
