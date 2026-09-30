import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {ApplicationSearchDatasource} from '@service/application/application-search-datasource';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import * as fromWorkQueue from '@feature/workqueue/reducers';
import {Observable} from 'rxjs';
import {ApplicationSearchQuery} from '@model/search/ApplicationSearchQuery';
import {filter} from 'rxjs/operators';

export class ApplicationWorkItemDatasource extends ApplicationSearchDatasource {
  constructor(store: Store<fromRoot.State>,
              paginator: MatPaginator,
              sort: MatSort) {
    super(store, paginator, sort);
  }

  protected initTargetType() {
    this.actionTargetType = ActionTargetType.ApplicationWorkQueue;
  }

  protected initSelectors() {
    this.resultSelector = fromWorkQueue.getMatchingApplications;
    this.searchParametersSelector = fromWorkQueue.getApplicationSearchParameters;
    this.sortSelector = fromWorkQueue.getApplicationSearchSort;
    this.pageRequestSelector = fromWorkQueue.getApplicationSearchPageRequest;
    this.searchingSelector = fromWorkQueue.getSearchingApplications;
  }

  protected get searchParameters(): Observable<ApplicationSearchQuery> {
    return this.store.pipe(select(this.searchParametersSelector), filter(params => !!params));
  }
}
