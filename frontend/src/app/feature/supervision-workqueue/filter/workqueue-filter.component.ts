import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {SupervisionTaskType} from '../../../model/application/supervision/supervision-task-type';
import {ApplicationType} from '../../../model/application/type/application-type';
import {SupervisionWorkItemStore} from '../supervision-work-item-store';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicationStatus} from '../../../model/application/application-status';
import {CityDistrict} from '../../../model/common/city-district';
import {Observable, Subject} from 'rxjs';
import {Sort} from '../../../model/common/sort';
import {SupervisionTaskSearchCriteria} from '../../../model/application/supervision/supervision-task-search-criteria';
import {StoredFilter} from '../../../model/user/stored-filter';
import {StoredFilterType} from '../../../model/user/stored-filter-type';
import {StoredFilterStore} from '../../../service/stored-filter/stored-filter-store';
import * as fromRoot from '../../allu/reducers';
import {Store} from '@ngrx/store';
import {debounceTime, distinctUntilChanged, filter, map, take, takeUntil} from 'rxjs/internal/operators';

interface TaskSearchFilter {
  search?: SupervisionTaskSearchCriteria;
  sort?: Sort;
}

@Component({
  selector: 'supervision-workqueue-filter',
  templateUrl: './workqueue-filter.component.html',
  styleUrls: [
    './workqueue-filter.component.scss'
  ]
})
export class WorkQueueFilterComponent implements OnInit, OnDestroy {
  queryForm: FormGroup;
  taskTypes = EnumUtil.enumValues(SupervisionTaskType);
  applicationTypes = EnumUtil.enumValues(ApplicationType);
  applicationStatusTypes = EnumUtil.enumValues(ApplicationStatus);
  districts: Observable<Array<CityDistrict>>;

  SUPERVISION_WORKQUEUE_FILTER = StoredFilterType.SUPERVISION_WORKQUEUE;
  taskFilter: Observable<TaskSearchFilter>;
  selectedFilter: Observable<StoredFilter>;
  defaultFilter: Observable<StoredFilter>;
  availableFilters: Observable<StoredFilter[]>;

  private destroy = new Subject<boolean>();

  constructor(
    private fb: FormBuilder,
    private itemStore: SupervisionWorkItemStore,
    private store: Store<fromRoot.State>,
    private storedFilterStore: StoredFilterStore) {
    this.queryForm = this.fb.group({
      taskTypes: [[]],
      applicationId: [undefined],
      after: [undefined],
      before: [undefined],
      applicationTypes: [[]],
      applicationStatus: [[]],
      ownerId: [undefined],
      cityDistrictIds: [[]]
    });
  }

  ngOnInit(): void {
    this.districts = this.store.select(fromRoot.getAllCityDistricts);

    this.itemStore.changes.pipe(
      map(state => state.search),
      take(1)
    ).subscribe(search => this.queryForm.patchValue(search, {emitEvent: false}));

    this.queryForm.valueChanges.pipe(
      takeUntil(this.destroy),
      distinctUntilChanged(),
      debounceTime(300)
    ) .subscribe(search => {
        this.storedFilterStore.resetCurrent(StoredFilterType.SUPERVISION_WORKQUEUE);
        this.itemStore.searchChange(search);
      });

    this.taskFilter = this.itemStore.changes.pipe(
      map(state => ({ search: state.search, sort: state.sort }))
    );

    this.selectedFilter = this.storedFilterStore.getCurrent(StoredFilterType.SUPERVISION_WORKQUEUE);
    this.availableFilters = this.storedFilterStore.getAvailable(StoredFilterType.SUPERVISION_WORKQUEUE);
    this.defaultFilter = this.storedFilterStore.getDefault(StoredFilterType.SUPERVISION_WORKQUEUE);

    this.storedFilterStore.getCurrentFilter(StoredFilterType.SUPERVISION_WORKQUEUE).pipe(
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
