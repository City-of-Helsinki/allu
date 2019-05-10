import {Geocoordinates} from '../../model/common/geocoordinates';
import {BackendGeocoordinates} from '../backend-model/backend-geocoordinates';
import {MapUtil} from '../map/map.util';
import {Projection} from '@feature/map/projection';


export class GeocoordinatesMapper {

  public static mapBackend(backendGeocoordinates: BackendGeocoordinates, projection: Projection): Geocoordinates {
    const coordinates = projection.unproject([backendGeocoordinates.x, backendGeocoordinates.y]);
    return Geocoordinates.fromArray(coordinates);
  }

  public static mapFrontend(geocoordinates: Geocoordinates, projection: Projection): BackendGeocoordinates {
    if (geocoordinates) {
      const coordinates = projection.project(geocoordinates.toArray());
      return {
        x: coordinates[0],
        y: coordinates[1]
      };
    } else {
      return undefined;
    }
  }
}
