import {Injectable} from '@angular/core';
import {Http, Response, URLSearchParams} from '@angular/http';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import { Observable }     from 'rxjs/Observable';

import {Location} from './location.class';
import {Geocoordinates} from '../model/common/geocoordinates';
import {GeocoordinatesMapper} from './mapper/geocoordinates-mapper';
import {StreetAddress} from '../model/common/street-address';
import {MapService} from './map.service';


@Injectable()
export class GeolocationService {

  static ADDRESS_URL = '/api/address';
  static GEOCODE_URL = '/geocode/helsinki';

  constructor(private authHttp: AuthHttp, private mapService: MapService) {}

  geocode(address: string): Observable<Geocoordinates> {
    let searchUrl = this.searchUrl(address);

    return this.authHttp.get(searchUrl)
      .map(response => response.json())
      .map(response => GeocoordinatesMapper.mapBackend(response, this.mapService));
  }

  private searchUrl(address: string) {
    let streetAddress = StreetAddress.fromAddressString(address);
    return GeolocationService.ADDRESS_URL + GeolocationService.GEOCODE_URL
      + '/' + streetAddress.streetName
      + '/' + streetAddress.streetNumber;
  }
}
