import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Observable, Subject} from 'rxjs';
import {ApplicationSearchQuery} from '../../../model/search/ApplicationSearchQuery';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicationStatus, workqueue_searchable} from '../../../model/application/application-status';
import {ApplicationType} from '../../../model/application/type/application-type';
import {User} from '../../../model/user/user';
import {ApplicationTagType} from '../../../model/application/tag/application-tag-type';
import {CityDistrict} from '../../../model/common/city-district';
import {WorkQueueTab} from '../workqueue-tab';
import {ApplicationWorkItemStore} from '../application-work-item-store';
import {StoredFilter} from '../../../model/user/stored-filter';
import {StoredFilterType} from '../../../model/user/stored-filter-type';
import {StoredFilterStore} from '../../../service/stored-filter/stored-filter-store';
import {Sort} from '../../../model/common/sort';
import * as fromRoot from '../../allu/reducers';
import {Store} from '@ngrx/store';
import {debounceTime, distinctUntilChanged, filter, map, take, takeUntil} from 'rxjs/internal/operators';

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
  applicationStatuses = workqueue_searchable.map(status => ApplicationStatus[status]);
  applicationTypes = EnumUtil.enumValues(ApplicationType);
  tagTypes = EnumUtil.enumValues(ApplicationTagType);
  tab: Observable<string>;

  WORKQUEUE_FILTER = StoredFilterType.WORKQUEUE;
  applicationFilter: Observable<ApplicationSearchFilter>;
  selectedFilter: Observable<StoredFilter>;
  defaultFilter: Observable<StoredFilter>;
  availableFilters: Observable<StoredFilter[]>;

  private destroy = new Subject<boolean>();

  constructor(fb: FormBuilder,
              private store: Store<fromRoot.State>,
              private itemStore: ApplicationWorkItemStore,
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

    this.itemStore.changes.pipe(
      map(state => state.search)
    ).subscribe(search => this.queryForm.patchValue(search, {emitEvent: false}));

    this.queryForm.valueChanges.pipe(
      takeUntil(this.destroy),
      distinctUntilChanged(),
      debounceTime(300),
    ).subscribe(query => {
        this.storedFilterStore.resetCurrent(StoredFilterType.WORKQUEUE);
        this.itemStore.searchChange(ApplicationSearchQuery.from(query));
      });

    this.tab = this.itemStore.changes.pipe(
      map(state => state.tab),
      map(tab => WorkQueueTab[tab])
    );

    this.applicationFilter = this.itemStore.changes.pipe(
      map(state => ({ search: state.search, sort: state.sort }))
    );

    this.selectedFilter = this.storedFilterStore.getCurrent(StoredFilterType.WORKQUEUE);
    this.availableFilters = this.storedFilterStore.getAvailable(StoredFilterType.WORKQUEUE);
    this.defaultFilter = this.storedFilterStore.getDefault(StoredFilterType.WORKQUEUE);

    this.storedFilterStore.getCurrentFilter(StoredFilterType.WORKQUEUE).pipe(
      takeUntil(this.destroy),
      filter(sf => !!sf),
      map(sf => sf.search)
    ).subscribe(search => this.itemStore.searchChange(search));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  selectFilter(storedFilter: StoredFilter) {
    this.storedFilterStore.currentChange(storedFilter);
  }
}
