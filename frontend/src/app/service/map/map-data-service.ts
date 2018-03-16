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
import {ApplicationStatus, ApplicationStatusGroup} from '../../model/application/application-status';
import {ArrayUtil} from '../../util/array-util';

const APPLICATION_SEARCH_URL = '/api/applications/search_location';

@Injectable()
export class MapDataService {
  private groupedStatuses = new Map<ApplicationStatusGroup, ApplicationStatus[]>();

  constructor(private authHttp: AuthHttp,
              private errorHandler: ErrorHandler,
              private mapUtil: MapUtil) {
    this.groupedStatuses.set(ApplicationStatusGroup.PRELIMINARY, [
      ApplicationStatus.PRE_RESERVED,
      ApplicationStatus.PENDING]);

    this.groupedStatuses.set(ApplicationStatusGroup.HANDLING, [
      ApplicationStatus.HANDLING,
      ApplicationStatus.RETURNED_TO_PREPARATION,
      ApplicationStatus.DECISIONMAKING
    ]);

    this.groupedStatuses.set(ApplicationStatusGroup.DECISION, [ApplicationStatus.DECISION]);

    this.groupedStatuses.set(ApplicationStatusGroup.HISTORY, [
      ApplicationStatus.REJECTED,
      ApplicationStatus.FINISHED,
      ApplicationStatus.CANCELLED,
      ApplicationStatus.ARCHIVED
    ]);
  }

  applicationsByLocation(filter: MapSearchFilter): Observable<Array<Application>> {
    if (filter.statuses && filter.statuses.length) {
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
    const statuses = this.statusesFromGroup(filter.statuses.map(sg => ApplicationStatusGroup[sg]));

    return new ApplicationLocationQuery(
      filter.startDate,
      filter.endDate,
      statuses,
      geometry);
  }

  private statusesFromGroup(groups: ApplicationStatusGroup[]): string[] {
    return ArrayUtil.flatten(groups.map(group => this.groupedStatuses.get(group)))
      .filter(s => !!s)
      .map(s => ApplicationStatus[s]);
  }
}
