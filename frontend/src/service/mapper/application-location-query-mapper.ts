import {ApplicationLocationQuery} from '../../model/search/ApplicationLocationQuery';
import {BackendApplicationLocationQuery} from '../backend-model/backend-application-location-query';
export class ApplicationLocationQueryMapper {

  public static mapFrontend(query: ApplicationLocationQuery): BackendApplicationLocationQuery {
    return query ? {
      after: (query.startDate) ? query.startDate.toISOString() : undefined,
      before: (query.endDate) ? query.endDate.toISOString() : undefined,
      intersectingGeometry: query.geometry
    } :
    undefined;
  }
}
