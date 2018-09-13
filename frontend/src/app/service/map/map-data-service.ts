import {Injectable} from '@angular/core';
import {findTranslation} from '../../util/translations';
import {ApplicationLocationQueryMapper} from '../mapper/application-location-query-mapper';
import {ApplicationMapper} from '../mapper/application-mapper';
import {Application} from '../../model/application/application';
import {ApplicationLocationQuery} from '../../model/search/ApplicationLocationQuery';
import {Observable, of} from 'rxjs';
import {ErrorHandler} from '../error/error-handler.service';
import {MapUtil} from './map.util';
import {MapSearchFilter} from '../map-search-filter';
import {ApplicationStatus, ApplicationStatusGroup} from '../../model/application/application-status';
import {ArrayUtil} from '../../util/array-util';
import {HttpClient} from '@angular/common/http';
import {BackendApplication} from '../backend-model/backend-application';
import {catchError, map} from 'rxjs/internal/operators';

const APPLICATION_SEARCH_URL = '/api/applications/search_location';

@Injectable()
export class MapDataService {
  private groupedStatuses = new Map<ApplicationStatusGroup, ApplicationStatus[]>();

  constructor(private http: HttpClient,
              private errorHandler: ErrorHandler,
              private mapUtil: MapUtil) {
    this.groupedStatuses.set(ApplicationStatusGroup.PRELIMINARY, [
      ApplicationStatus.PRE_RESERVED,
      ApplicationStatus.PENDING_CLIENT,
      ApplicationStatus.PENDING]);

    this.groupedStatuses.set(ApplicationStatusGroup.HANDLING, [
      ApplicationStatus.HANDLING,
      ApplicationStatus.RETURNED_TO_PREPARATION,
      ApplicationStatus.DECISIONMAKING
    ]);

    this.groupedStatuses.set(ApplicationStatusGroup.DECISION, [
      ApplicationStatus.DECISION,
      ApplicationStatus.OPERATIONAL_CONDITION
    ]);

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
      return this.http.post<BackendApplication[]>(
        APPLICATION_SEARCH_URL,
        JSON.stringify(ApplicationLocationQueryMapper.mapFrontend(query))).pipe(
        map(applications => applications.map(app => ApplicationMapper.mapCommon(app))),
        catchError(error => this.errorHandler.handle(error, findTranslation('application.error.searchFailed')))
      );
    } else {
      return of([]);
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
      .filter(s => s !== undefined)
      .map(s => ApplicationStatus[s]);
  }
}
