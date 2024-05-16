import {combineLatest, Observable} from 'rxjs';
import {MatLegacyPaginator as MatPaginator} from '@angular/material/legacy-paginator';
import {MatSort} from '@angular/material/sort';
import {Application} from '@model/application/application';
import {filter, takeUntil} from 'rxjs/operators';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {ApplicationSearchQuery} from '@model/search/ApplicationSearchQuery';
import {StoreDatasource} from '@service/common/store-datasource';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Search, SetPaging, SetSort} from '@feature/application/actions/application-search-actions';
import {PageRequest} from '@model/common/page-request';
import {Sort} from '@model/common/sort';

export class ApplicationSearchDatasource extends StoreDatasource<Application> {

  protected searchParametersSelector = fromApplication.getApplicationSearchParameters;

  constructor(store: Store<fromRoot.State>,
              paginator: MatPaginator,
              sort: MatSort) {
    super(store, paginator, sort);
  }

  protected initTargetType() {
    this.actionTargetType = ActionTargetType.Application;
  }

  protected initSelectors() {
    this.resultSelector = fromApplication.getMatchingApplications;
    this.searchParametersSelector = fromApplication.getApplicationSearchParameters;
    this.sortSelector = fromApplication.getApplicationSearchSort;
    this.pageRequestSelector = fromApplication.getApplicationSearchPageRequest;
    this.searchingSelector = fromApplication.getSearchingApplications;
  }

  protected dispatchPageRequest(pageRequest: PageRequest): void {
    this.store.dispatch(new SetPaging(this.actionTargetType, pageRequest));
  }

  protected dispatchSort(sort: Sort): void {
    this.store.dispatch(new SetSort(this.actionTargetType, sort));
  }

  /**
   * Can be overridden for getting search parameters
   */
  protected get searchParameters(): Observable<ApplicationSearchQuery> {
    return this.store.pipe(select(this.searchParametersSelector), filter(params => !!params));
  }

  protected setupSearch(): void {
    combineLatest([
      this.searchParameters,
      this.store.pipe(select(this.sortSelector)),
      this.store.pipe(select(this.pageRequestSelector)),
    ]).pipe(
      takeUntil(this.destroy)
    ).subscribe(([search, sort, paging]) => {
      this.store.dispatch(new Search(this.actionTargetType, search, sort, paging));
    });
  }
}
