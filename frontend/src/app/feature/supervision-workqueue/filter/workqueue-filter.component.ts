import {Component, OnDestroy, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {SupervisionTaskType} from '@model/application/supervision/supervision-task-type';
import {ApplicationType} from '@model/application/type/application-type';
import {EnumUtil} from '@util/enum.util';
import {ApplicationStatus} from '@model/application/application-status';
import {CityDistrict} from '@model/common/city-district';
import {combineLatest, Observable, Subject} from 'rxjs';
import {Sort} from '@model/common/sort';
import {SupervisionTaskSearchCriteria} from '@model/application/supervision/supervision-task-search-criteria';
import {StoredFilter} from '@model/user/stored-filter';
import {StoredFilterType} from '@model/user/stored-filter-type';
import {StoredFilterStore} from '@service/stored-filter/stored-filter-store';
import {ArrayUtil} from '@util/array-util';
import {UserService} from '@service/user/user-service';
import {User} from '@model/user/user';
import {RoleType} from '@model/user/role-type';
import * as fromRoot from '@feature/allu/reducers';
import * as fromSupervisionWorkQueue from '@feature/supervision-workqueue/reducers';
import {select, Store} from '@ngrx/store';
import {debounceTime, distinctUntilChanged, filter, map, take, takeUntil} from 'rxjs/internal/operators';
import {SetSearchQuery} from '@feature/application/supervision/actions/supervision-task-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

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
  queryForm: UntypedFormGroup;
  taskTypes = EnumUtil.enumValues(SupervisionTaskType);
  applicationTypes = Object.keys(ApplicationType)
    .sort(ArrayUtil.naturalSortTranslated(['application.type'], (type: string) => type));
  applicationStatusTypes = Object.keys(ApplicationStatus);
  districts: Observable<Array<CityDistrict>>;
  supervisors: Array<User> = [];

  SUPERVISION_WORKQUEUE_FILTER = StoredFilterType.SUPERVISION_WORKQUEUE;
  taskFilter: Observable<TaskSearchFilter>;
  selectedFilter: Observable<StoredFilter>;
  defaultFilter: Observable<StoredFilter>;
  availableFilters: Observable<StoredFilter[]>;

  private destroy = new Subject<boolean>();

  constructor(
    private fb: UntypedFormBuilder,
    private store: Store<fromRoot.State>,
    private storedFilterStore: StoredFilterStore,
    private userService: UserService) {
    this.queryForm = this.fb.group({
      taskTypes: [[]],
      applicationId: [undefined],
      after: [undefined],
      before: [undefined],
      applicationTypes: [[]],
      applicationStatus: [[]],
      owners: [[]],
      cityDistrictIds: [[]]
    });
  }

  ngOnInit(): void {
    this.districts = this.store.select(fromRoot.getAllCityDistricts);
    this.userService.getByRole(RoleType.ROLE_SUPERVISE).subscribe(
      users => this.supervisors = users.sort(ArrayUtil.naturalSort((user: User) => user.realName)));

    this.store.pipe(
      select(fromSupervisionWorkQueue.getParameters),
      take(1)
    ).subscribe(search => this.onSearchChange(search));

    this.queryForm.valueChanges.pipe(
      takeUntil(this.destroy),
      distinctUntilChanged(),
      debounceTime(300)
    ) .subscribe(search => {
      this.storedFilterStore.resetCurrent(StoredFilterType.SUPERVISION_WORKQUEUE);
      const query = SupervisionTaskSearchCriteria.updateDatesForSearch(search);
      this.store.dispatch(new SetSearchQuery(ActionTargetType.SupervisionTaskWorkQueue, query));
    });

    this.taskFilter = combineLatest([
      this.store.pipe(select(fromSupervisionWorkQueue.getParameters)),
      this.store.pipe(select(fromSupervisionWorkQueue.getSort))
    ]).pipe(
      map(([search, sort]) => ({ search, sort }))
    );

    this.selectedFilter = this.storedFilterStore.getCurrent(StoredFilterType.SUPERVISION_WORKQUEUE);
    this.availableFilters = this.storedFilterStore.getAvailable(StoredFilterType.SUPERVISION_WORKQUEUE);
    this.defaultFilter = this.storedFilterStore.getDefault(StoredFilterType.SUPERVISION_WORKQUEUE);

    this.storedFilterStore.getCurrentFilter(StoredFilterType.SUPERVISION_WORKQUEUE).pipe(
      takeUntil(this.destroy),
      filter(sf => !!sf),
      map(sf => sf.search)
    ).subscribe(search => {
      this.queryForm.patchValue(search, {emitEvent: false});
      const query = SupervisionTaskSearchCriteria.updateDatesForSearch(search);
      this.store.dispatch(new SetSearchQuery(ActionTargetType.SupervisionTaskWorkQueue, query));
    });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  selectFilter(storedFilter: StoredFilter) {
    this.storedFilterStore.currentChange(storedFilter);
  }

  private onSearchChange(search: SupervisionTaskSearchCriteria) {
    if (search) {
      this.queryForm.patchValue(search, {emitEvent: false});
    } else {
      this.store.dispatch(new SetSearchQuery(ActionTargetType.SupervisionTaskWorkQueue, new SupervisionTaskSearchCriteria()));
    }
  }
}
