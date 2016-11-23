import {Injectable} from '@angular/core';
import {ApplicationSearchQuery} from '../model/search/ApplicationSearchQuery';
import {Application} from '../model/application/application';
import {QueryParametersMapper} from './mapper/query-parameters-mapper';
import {ApplicationMapper} from './mapper/application-mapper';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';

@Injectable()
export class SearchService {

  static APPLICATIONS_SEARCH_URL = '/api/applications/search';

  constructor(private authHttp: AuthHttp) {}

  public searchApplication(query: ApplicationSearchQuery): Promise<Array<Application>> {
    console.log('SearchService.searchApplication', query);
    return new Promise<Array<Application>>((resolve, reject) =>
        this.authHttp.post(
          SearchService.APPLICATIONS_SEARCH_URL,
          JSON.stringify(QueryParametersMapper.mapApplicationQueryFrontend(query))).subscribe(
        data => {
          console.log('SearchService.searchApplication post response', data);
          let json = data.json();
          if (json) {
            let applications: Array<Application> = json.map((a) => ApplicationMapper.mapBackend(a));
            console.log('Search found', applications);
            resolve(applications);
          } else {
            resolve([]);
          }
        },
        err => console.log('SearchService.searchApplication', err),
        () => console.log('Request Complete')
      )
    );
  }
}
