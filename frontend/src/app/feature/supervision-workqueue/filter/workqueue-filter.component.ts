import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {SupervisionTaskType} from '../../../model/application/supervision/supervision-task-type';
import {ApplicationType} from '../../../model/application/type/application-type';
import {SupervisionWorkItemStore} from '../supervision-work-item-store';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicationStatus} from '../../../model/application/application-status';
import {CityDistrict} from '../../../model/common/city-district';
import {Observable} from 'rxjs/Observable';
import {CityDistrictService} from '../../../service/map/city-district.service';
import {Sort} from '../../../model/common/sort';
import {SupervisionTaskSearchCriteria} from '../../../model/application/supervision/supervision-task-search-criteria';
import {StoredFilter} from '../../../model/user/stored-filter';
import {StoredFilterType} from '../../../model/user/stored-filter-type';
import {StoredFilterStore} from '../../../service/stored-filter/stored-filter-store';
import {Subject} from 'rxjs/Subject';

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
    private store: SupervisionWorkItemStore,
    private cityDistrictService: CityDistrictService,
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
    this.queryForm.patchValue(this.store.snapshot.search, {emitEvent: false});

    this.store.changes.map(state => state.search)
      .takeUntil(this.destroy)
      .subscribe(search => this.queryForm.patchValue(search, {emitEvent: false}));

    this.queryForm.valueChanges
      .takeUntil(this.destroy)
      .distinctUntilChanged()
      .debounceTime(300)
      .subscribe(search => {
        this.storedFilterStore.resetCurrent(StoredFilterType.SUPERVISION_WORKQUEUE);
        this.store.searchChange(search);
      });

    this.districts = this.cityDistrictService.get();

    this.taskFilter = this.store.changes
      .map(state => ({ search: state.search, sort: state.sort }));

    this.selectedFilter = this.storedFilterStore.getCurrent(StoredFilterType.SUPERVISION_WORKQUEUE);
    this.availableFilters = this.storedFilterStore.getAvailable(StoredFilterType.SUPERVISION_WORKQUEUE);
    this.defaultFilter = this.storedFilterStore.getDefault(StoredFilterType.SUPERVISION_WORKQUEUE);

    this.storedFilterStore.getCurrentFilter(StoredFilterType.SUPERVISION_WORKQUEUE)
      .takeUntil(this.destroy)
      .filter(filter => !!filter)
      .map(filter => filter.search)
      .subscribe(search => this.store.searchChange(search));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  selectFilter(filter: StoredFilter) {
    this.storedFilterStore.currentChange(filter);
  }
}
