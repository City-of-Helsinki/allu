import {MatPaginator, MatSort} from '@angular/material';
import {ApplicationSearchDatasource} from '@service/application/application-search-datasource';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import * as fromWorkQueue from '@feature/workqueue/reducers';
import * as fromAuth from '@feature/auth/reducers';
import {combineLatest, Observable} from 'rxjs';
import {ApplicationSearchQuery} from '@model/search/ApplicationSearchQuery';
import {filter, map} from 'rxjs/operators';
import {WorkQueueTab} from '@feature/workqueue/workqueue-tab';
import {ObjectUtil} from '@util/object.util';

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
    return combineLatest(
      this.store.pipe(select(this.searchParametersSelector), filter(params => !!params)),
      this.store.pipe(select(fromWorkQueue.getTab)),
      this.store.pipe(select(fromAuth.getUser), filter(user => !!user))
    ).pipe(
      map(([query, tab, user]) => {
        const queryCopy = ObjectUtil.clone(query);
        queryCopy.owner = WorkQueueTab.OWN === tab ? [user.userName] : query.owner;
        return queryCopy;
      })
    );
  }
}
