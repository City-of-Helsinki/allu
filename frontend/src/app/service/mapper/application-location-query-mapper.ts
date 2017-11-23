import {ApplicationLocationQuery} from '../../model/search/ApplicationLocationQuery';
import {BackendApplicationLocationQuery} from '../backend-model/backend-application-location-query';
import {Some} from '../../util/option';
export class ApplicationLocationQueryMapper {

  public static mapFrontend(query: ApplicationLocationQuery): BackendApplicationLocationQuery {
    return query ? {
      after: (query.startDate) ? query.startDate.toISOString() : undefined,
      before: (query.endDate) ? query.endDate.toISOString() : undefined,
      statusTypes: Some(query.statusTypes).orElse([]),
      intersectingGeometry: query.geometry
    } :
    undefined;
  }
}
