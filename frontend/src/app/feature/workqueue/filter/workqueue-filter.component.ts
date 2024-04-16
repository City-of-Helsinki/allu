import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {combineLatest, Observable, Subject} from 'rxjs';
import {ApplicationSearchQuery} from '@model/search/ApplicationSearchQuery';
import {EnumUtil} from '@util/enum.util';
import {workqueue_searchable} from '@model/application/application-status';
import {ApplicationType} from '@model/application/type/application-type';
import {User} from '@model/user/user';
import {ApplicationTagType} from '@model/application/tag/application-tag-type';
import {CityDistrict} from '@model/common/city-district';
import {WorkQueueTab} from '../workqueue-tab';
import {StoredFilter} from '@model/user/stored-filter';
import {StoredFilterType} from '@model/user/stored-filter-type';
import {StoredFilterStore} from '@service/stored-filter/stored-filter-store';
import {Sort} from '@model/common/sort';
import * as fromRoot from '@feature/allu/reducers';
import * as fromWorkQueue from '@feature/workqueue/reducers';
import {select, Store} from '@ngrx/store';
import {debounceTime, distinctUntilChanged, filter, map, takeUntil} from 'rxjs/operators';
import {ArrayUtil} from '@util/array-util';
import {SetSearchQuery} from '@feature/application/actions/application-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

interface ApplicationSearchFilter {
  search?: ApplicationSearchQuery;
  sort?: Sort;
}

@Component({
  selector: 'workqueue-filter',
  templateUrl: './workqueue-filter.component.html',
  styleUrls: [
    './workqueue-filter.component.scss'
  ]
})
export class WorkQueueFilterComponent implements OnInit, OnDestroy {

  @Input() owners: Array<User>;

  queryForm: FormGroup;
  districts: Observable<Array<CityDistrict>>;
  applicationStatuses = workqueue_searchable;
  applicationTypes = Object.keys(ApplicationType)
    .sort(ArrayUtil.naturalSortTranslated(['application.type'], (type: string) => type));
  tagTypes = EnumUtil.enumValues(ApplicationTagType);
  tab$: Observable<WorkQueueTab>;

  WORKQUEUE_FILTER = StoredFilterType.WORKQUEUE;
  applicationFilter: Observable<ApplicationSearchFilter>;
  selectedFilter: Observable<StoredFilter>;
  defaultFilter: Observable<StoredFilter>;
  availableFilters: Observable<StoredFilter[]>;

  private destroy = new Subject<boolean>();

  constructor(fb: FormBuilder,
              private store: Store<fromRoot.State>,
              private storedFilterStore: StoredFilterStore) {
    this.queryForm = fb.group({
      type: undefined,
      owner: undefined,
      status: undefined,
      districts: undefined,
      startTime: undefined,
      endTime: undefined,
      tags: []
    });
  }

  ngOnInit(): void {
    this.districts = this.store.select(fromRoot.getAllCityDistricts);

    this.store.pipe(
      select(fromWorkQueue.getApplicationSearchParameters)
    ).subscribe(search => this.onSearchChange(search));

    this.queryForm.valueChanges.pipe(
      takeUntil(this.destroy),
      distinctUntilChanged(),
      debounceTime(300),
    ).subscribe(query => {
      this.storedFilterStore.resetCurrent(StoredFilterType.WORKQUEUE);
      this.store.dispatch(new SetSearchQuery(ActionTargetType.ApplicationWorkQueue, query));
    });

    this.tab$ = this.store.pipe(select(fromWorkQueue.getTab));

    this.applicationFilter = combineLatest([
      this.store.pipe(select(fromWorkQueue.getApplicationSearchParameters)),
      this.store.pipe(select(fromWorkQueue.getApplicationSearchSort)),
    ]).pipe(
      map(([search, sort]) => ({ search, sort }))
    );

    this.selectedFilter = this.storedFilterStore.getCurrent(StoredFilterType.WORKQUEUE);
    this.availableFilters = this.storedFilterStore.getAvailable(StoredFilterType.WORKQUEUE);
    this.defaultFilter = this.storedFilterStore.getDefault(StoredFilterType.WORKQUEUE);

    this.storedFilterStore.getCurrentFilter(StoredFilterType.WORKQUEUE).pipe(
      takeUntil(this.destroy),
      filter(sf => !!sf),
      map(sf => sf.search)
    ).subscribe(search => {
      this.queryForm.patchValue(search, {emitEvent: false});
      this.store.dispatch(new SetSearchQuery(ActionTargetType.ApplicationWorkQueue, search));
    });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  selectFilter(storedFilter: StoredFilter) {
    this.storedFilterStore.currentChange(storedFilter);
  }

  private onSearchChange(search: ApplicationSearchQuery): void {
    if (search) {
      this.queryForm.patchValue(search, {emitEvent: false});
    } else {
      this.store.dispatch(new SetSearchQuery(ActionTargetType.ApplicationWorkQueue, new ApplicationSearchQuery()));
    }
  }
}
