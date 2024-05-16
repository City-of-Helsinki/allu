import {combineLatest} from 'rxjs';
import {MatLegacyPaginator as MatPaginator} from '@angular/material/legacy-paginator';
import {MatSort} from '@angular/material/sort';
import {Project} from '@model/project/project';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';
import {filter, takeUntil} from 'rxjs/internal/operators';
import {StoreDatasource} from '@service/common/store-datasource';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import * as fromProject from '@feature/project/reducers';
import {Search, SetPaging, SetSort} from '@feature/project/actions/project-search-actions';

export class ProjectSearchDatasource extends StoreDatasource<Project> {
  constructor(store: Store<fromRoot.State>,
              paginator: MatPaginator,
              sort: MatSort) {
    super(store, paginator, sort);
  }

  protected initSelectors(): void {
    this.actionTargetType = ActionTargetType.Project;
  }

  protected initTargetType(): void {
    this.resultSelector = fromProject.getMatching;
    this.sortSelector = fromProject.getSort;
    this.pageRequestSelector = fromProject.getPageRequest;
    this.searchingSelector = fromProject.getSearching;
  }

  protected dispatchPageRequest(pageRequest: PageRequest): void {
    this.store.dispatch(new SetPaging(this.actionTargetType, pageRequest));
  }

  protected dispatchSort(sort: Sort): void {
    this.store.dispatch(new SetSort(this.actionTargetType, sort));
  }

  protected setupSearch(): void {
    combineLatest([
      this.store.pipe(select(fromProject.getParameters), filter(search => !!search)),
      this.store.pipe(select(this.sortSelector)),
      this.store.pipe(select(this.pageRequestSelector))
    ]).pipe(
      takeUntil(this.destroy)
    ).subscribe(([query, sort, pageRequest]) =>
      this.store.dispatch(new Search(query, sort, pageRequest)));
  }
}
