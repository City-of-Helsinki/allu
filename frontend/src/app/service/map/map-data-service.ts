import {Injectable} from '@angular/core';
import {findTranslation} from '../../util/translations';
import {ApplicationMapper} from '../mapper/application-mapper';
import {Application} from '../../model/application/application';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {ErrorHandler} from '../error/error-handler.service';
import {MapUtil} from './map.util';
import {MapSearchFilter} from '../map-search-filter';
import {ApplicationStatus, ApplicationStatusGroup} from '../../model/application/application-status';
import {ArrayUtil} from '../../util/array-util';
import {HttpClient} from '@angular/common/http';
import {SearchResultApplication} from '../backend-model/backend-application';
import {catchError, map, reduce, switchMap, tap} from 'rxjs/operators';
import {QueryParametersMapper} from '@app/service/mapper/query/query-parameters-mapper';
import {BackendQueryParameter, BackendQueryParameters} from '@app/service/backend-model/backend-query-parameters';
import {PageMapper} from '@app/service/common/page-mapper';
import {BackendPage} from '@app/service/backend-model/backend-page';
import {PageRequest} from '@app/model/common/page-request';
import {Sort} from '@app/model/common/sort';
import {TimeUtil} from '@util/time.util';
import {Page} from '@model/common/page';


const APPLICATION_SEARCH_URL = '/api/applications/search';
const PAGE_SIZE = 1000;

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
      ApplicationStatus.TERMINATED,
      ApplicationStatus.NOTE
    ]);

    this.groupedStatuses.set(ApplicationStatusGroup.HISTORY, [
      ApplicationStatus.REJECTED,
      ApplicationStatus.FINISHED,
      ApplicationStatus.CANCELLED,
      ApplicationStatus.ARCHIVED
    ]);
  }

  applicationsByLocation(filter: MapSearchFilter, includeSurveyRequired?: boolean): Observable<Application[]> {
    if (filter.geometry && !ArrayUtil.empty(filter.statuses) && !ArrayUtil.empty(filter.types)) {
      const query = this.toApplicationLocationQuery(filter, includeSurveyRequired);
      const loadMore = new BehaviorSubject<PageRequest>(new PageRequest(0, PAGE_SIZE));

      return loadMore.pipe(
        switchMap(pr => this.loadPage(query, pr)),
        tap(page => {
          if (page.last || (page.size < PAGE_SIZE)) {
            loadMore.complete();
          } else {
            loadMore.next(new PageRequest(page.pageNumber + 1, PAGE_SIZE));
          }
        }),
        reduce((acc, page) => acc.concat(page.content), []),
      );
    } else {
      return of([]);
    }
  }

  private loadPage(query: BackendQueryParameters, pageRequest: PageRequest): Observable<Page<Application>> {
    return this.http.post<BackendPage<SearchResultApplication>>(
      APPLICATION_SEARCH_URL,
      JSON.stringify(query),
      {params: QueryParametersMapper.mapPageRequest(pageRequest, new Sort('applicationId', 'asc'))})
      .pipe(
        map(backendPage => PageMapper.mapBackend(backendPage, ApplicationMapper.mapSearchResult)),
        catchError(error => this.errorHandler.handle(error, findTranslation('application.error.searchFailed')))
      );
  }

  private toApplicationLocationQuery(filter: MapSearchFilter, includeSurveyRequired?: boolean): BackendQueryParameters {
    const viewPoly = this.mapUtil.polygonFromBounds(filter.geometry);
    const geometry = this.mapUtil.featureToGeometry(viewPoly.toGeoJSON());
    const zoom = filter.zoom;
    return {
      queryParameters: this.mapSearchParameters(filter),
      intersectingGeometry: geometry,
      surveyRequired: includeSurveyRequired,
      zoom: zoom
    };
  }

  private  mapSearchParameters(filter: MapSearchFilter): Array<BackendQueryParameter> {
    const queryParameters: Array<BackendQueryParameter> = [];
    const statuses = this.statusesFromGroup(filter.statuses);
    QueryParametersMapper.mapArrayParameter(queryParameters, 'status', statuses);
    QueryParametersMapper.mapDateParameter(
      queryParameters,
      'recurringApplication',
      TimeUtil.toStartDate(filter.startDate),
      TimeUtil.toEndDate(filter.endDate),
      true);
    QueryParametersMapper.mapArrayParameter(queryParameters, 'type', filter.types);
    return queryParameters;
  }

  private statusesFromGroup(groups: ApplicationStatusGroup[]): ApplicationStatus[] {
    return ArrayUtil.flatten(groups.map(group => this.groupedStatuses.get(group)))
      .filter(s => s !== undefined);
  }
}
