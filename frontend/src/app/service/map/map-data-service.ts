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
import {SearchResultApplication} from '../backend-model/backend-application';
import {catchError, map} from 'rxjs/internal/operators';
import {QueryParametersMapper} from '@app/service/mapper/query/query-parameters-mapper';
import {BackendQueryParameter, BackendQueryParameters} from '@app/service/backend-model/backend-query-parameters';
import {PageMapper} from '@app/service/common/page-mapper';
import {BackendPage} from '@app/service/backend-model/backend-page';
import {PageRequest} from '@app/model/common/page-request';
import {Sort} from '@app/model/common/sort';


const APPLICATION_SEARCH_URL = '/api/applications/search';

@Injectable()
export class MapDataService {
  private groupedStatuses = new Map<ApplicationStatusGroup, ApplicationStatus[]>();

  constructor(private http: HttpClient,
              private errorHandler: ErrorHandler,
              private mapUtil: MapUtil) {
    this.groupedStatuses.set(ApplicationStatusGroup.PRELIMINARY, [
      ApplicationStatus.PRE_RESERVED,
      ApplicationStatus.PENDING_CLIENT,
      ApplicationStatus.PENDING,
      ApplicationStatus.NOTE
    ]);

    this.groupedStatuses.set(ApplicationStatusGroup.HANDLING, [
      ApplicationStatus.HANDLING,
      ApplicationStatus.RETURNED_TO_PREPARATION,
      ApplicationStatus.DECISIONMAKING,
      ApplicationStatus.WAITING_CONTRACT_APPROVAL,
      ApplicationStatus.WAITING_INFORMATION,
      ApplicationStatus.INFORMATION_RECEIVED,
      ApplicationStatus.NOTE
    ]);

    this.groupedStatuses.set(ApplicationStatusGroup.DECISION, [
      ApplicationStatus.DECISION,
      ApplicationStatus.OPERATIONAL_CONDITION,
      ApplicationStatus.NOTE
    ]);

    this.groupedStatuses.set(ApplicationStatusGroup.HISTORY, [
      ApplicationStatus.REJECTED,
      ApplicationStatus.FINISHED,
      ApplicationStatus.CANCELLED,
      ApplicationStatus.ARCHIVED
    ]);
  }

  applicationsByLocation(filter: MapSearchFilter): Observable<Application[]> {
    if (filter.geometry && filter.statuses && filter.statuses.length) {
      const query = this.toApplicationLocationQuery(filter);
      return this.http.post<BackendPage<SearchResultApplication>>(
        APPLICATION_SEARCH_URL,
        JSON.stringify(query),
        {params: QueryParametersMapper.mapPageRequest(new PageRequest(), new Sort('applicationId', 'asc'))})
        .pipe(
          map(backendPage => PageMapper.mapBackend(backendPage, ApplicationMapper.mapSearchResult)),
          map(page => page.content),
        catchError(error => this.errorHandler.handle(error, findTranslation('application.error.searchFailed')))
      );
    } else {
      return of([]);
    }
  }

  private toApplicationLocationQuery(filter: MapSearchFilter): BackendQueryParameters {
    const viewPoly = this.mapUtil.polygonFromBounds(filter.geometry);
    const geometry = this.mapUtil.featureToGeometry(viewPoly.toGeoJSON());
    return {
      queryParameters: this.mapSearchParameters(filter),
      intersectingGeometry: geometry
    };
  }

  private  mapSearchParameters(filter: MapSearchFilter): Array<BackendQueryParameter> {
    const queryParameters: Array<BackendQueryParameter> = [];
    const statuses = this.statusesFromGroup(filter.statuses);
    QueryParametersMapper.mapArrayParameter(queryParameters, 'status', statuses);
    QueryParametersMapper.mapDateParameter(queryParameters, 'recurringApplication', filter.startDate, filter.endDate, true);
    return queryParameters;
  }

  private statusesFromGroup(groups: ApplicationStatusGroup[]): ApplicationStatus[] {
    return ArrayUtil.flatten(groups.map(group => this.groupedStatuses.get(group)))
      .filter(s => s !== undefined);
  }

}
