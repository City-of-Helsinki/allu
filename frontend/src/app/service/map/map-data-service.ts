import {Injectable} from '@angular/core';
import {findTranslation} from '../../util/translations';
import {ApplicationLocationQueryMapper} from '../mapper/application-location-query-mapper';
import {ApplicationMapper} from '../mapper/application-mapper';
import {Application} from '../../model/application/application';
import {ApplicationLocationQuery} from '../../model/search/ApplicationLocationQuery';
import {Observable} from 'rxjs/Observable';
import {AuthHttp} from 'angular2-jwt';
import {ErrorHandler} from '../error/error-handler.service';
import {MapUtil} from './map.util';
import {MapSearchFilter} from '../map-search-filter';

const APPLICATION_SEARCH_URL = '/api/applications/search_location';

@Injectable()
export class MapDataService {
  constructor(private authHttp: AuthHttp,
              private errorHandler: ErrorHandler,
              private mapUtil: MapUtil) {}

  applicationsByLocation(filter: MapSearchFilter): Observable<Array<Application>> {
    if (filter.statusTypes && filter.statusTypes.length) {
      const query = this.toApplicationLocationQuery(filter);
      return this.authHttp.post(
        APPLICATION_SEARCH_URL,
        JSON.stringify(ApplicationLocationQueryMapper.mapFrontend(query)))
        .map(response => response.json())
        .map(json => json.map(app => ApplicationMapper.mapBackend(app)))
        .catch(error => this.errorHandler.handle(error, findTranslation('application.error.searchFailed')));
    } else {
      return Observable.of([]);
    }
  }

  private toApplicationLocationQuery(filter: MapSearchFilter) {
    const viewPoly = this.mapUtil.polygonFromBounds(filter.geometry);
    const geometry = this.mapUtil.featureToGeometry(viewPoly.toGeoJSON());

    return new ApplicationLocationQuery(
      filter.startDate,
      filter.endDate,
      filter.statusTypes,
      geometry);
  }
}
