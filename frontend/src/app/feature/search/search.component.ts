import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {distinctUntilChanged, map, takeUntil} from 'rxjs/internal/operators';

import {Application} from '../../model/application/application';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {ApplicationStatus, searchable} from '../../model/application/application-status';
import {ApplicationWorkItemStore} from '../workqueue/application-work-item-store';
import {EnumUtil} from '../../util/enum.util';
import {ApplicationType} from '../../model/application/type/application-type';
import {UserHub} from '../../service/user/user-hub';
import {User} from '../../model/user/user';
import {FormBuilder, FormGroup} from '@angular/forms';
import {CityDistrict} from '../../model/common/city-district';
import {ApplicationService} from '../../service/application/application.service';
import {MatCheckboxChange, MatPaginator, MatSort} from '@angular/material';
import {ApplicationSearchDatasource} from '../../service/application/application-search-datasource';
import {NotificationService} from '../../service/notification/notification.service';
import {ArrayUtil} from '../../util/array-util';
import {AddMultiple} from '../project/actions/application-basket-actions';
import * as fromRoot from '../allu/reducers';
import {Store} from '@ngrx/store';

@Component({
  selector: 'search',
  templateUrl: './search.component.html'
})
export class SearchComponent implements OnInit, OnDestroy {

  displayedColumns = [
    'selected', 'owner.realName', 'applicationId', 'name', 'type', 'status', 'project.identifier',
    'customers.applicant.customer.name', 'locations.address', 'locations.cityDistrictId',
    'creationTime', 'startTime'
  ];

  queryForm: FormGroup;
  applications: Array<Application>;
  owners: Observable<Array<User>>;
  districts: Observable<Array<CityDistrict>>;
  applicationStatusStrings = searchable.map(status => ApplicationStatus[status]);
  applicationTypeStrings = EnumUtil.enumValues(ApplicationType)
    .sort(ArrayUtil.naturalSortTranslated(['application.type'], (type: string) => type));
  dataSource: ApplicationSearchDatasource;
  allSelected = false;
  noneSelected = true;

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  private selectedItems: Array<number> = [];
  private destroy = new Subject<boolean>();

  constructor(private applicationService: ApplicationService,
              private itemStore: ApplicationWorkItemStore,
              private userHub: UserHub,
              private store: Store<fromRoot.State>,
              private fb: FormBuilder,
              private notification: NotificationService) {
    this.queryForm = this.fb.group({
      applicationId: undefined,
      type: undefined,
      status: undefined,
      districts: undefined,
      owner: undefined,
      address: undefined,
      applicant: undefined,
      contact: undefined,
      freeText: undefined,
      startTime: undefined,
      endTime: undefined
    });
  }

  ngOnInit(): void {
    this.dataSource = new ApplicationSearchDatasource(this.applicationService, this.notification, this.paginator, this.sort);
    this.owners = this.userHub.getActiveUsers().pipe(
      map(users => users.sort(ArrayUtil.naturalSort((user: User) => user.realName))));
    this.districts = this.store.select(fromRoot.getAllCityDistricts);

    this.itemStore.changes.pipe(
      map(state => state.selectedItems),
      distinctUntilChanged(),
      takeUntil(this.destroy)
    ).subscribe(selected => this.selectedItems = selected);

    this.itemStore.changes.pipe(
      map(state => state.allSelected),
      distinctUntilChanged(),
      takeUntil(this.destroy)
    ).subscribe(allSelected => this.allSelected = allSelected);

    this.itemStore.changes.pipe(
      map(state => state.selectedItems),
      distinctUntilChanged(),
      takeUntil(this.destroy),
    ).subscribe(items => this.noneSelected = (items.length === 0));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  search(): void {
    const query = ApplicationSearchQuery.from(this.queryForm.value);
    this.dataSource.searchChange(query);
  }

  districtName(id: number): Observable<string> {
    return this.store.select(fromRoot.getCityDistrictName(id));
  }

  trackById(index: number, item: Application) {
    return item.id;
  }

  selected(id: number): boolean {
    return this.selectedItems.indexOf(id) >= 0;
  }

  checkAll(change: MatCheckboxChange): void {
    this.itemStore.toggleAll(change.checked);
  }

  checkSingle(change: MatCheckboxChange, taskId: number) {
    this.itemStore.toggleSingle(taskId, change.checked);
  }

  addToBasket(): void {
    const selected = this.itemStore.snapshot.selectedItems;
    this.store.dispatch(new AddMultiple(selected));
    this.itemStore.selectedItemsChange([]);
  }
}
