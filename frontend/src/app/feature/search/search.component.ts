import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {map, take} from 'rxjs/operators';

import {Application} from '@model/application/application';
import {ApplicationSearchQuery, toForm} from '@model/search/ApplicationSearchQuery';
import {searchable} from '@model/application/application-status';
import {ApplicationType} from '@model/application/type/application-type';
import {UserService} from '@service/user/user-service';
import {User} from '@model/user/user';
import {RoleType} from '@model/user/role-type';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {CityDistrict} from '@model/common/city-district';
import {MatLegacyPaginator as MatPaginator} from '@angular/material/legacy-paginator';
import {MatSort} from '@angular/material/sort';
import {ArrayUtil} from '@util/array-util';
import {AddMultiple} from '@feature/project/actions/application-basket-actions';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {select, Store} from '@ngrx/store';
import {ClearSelected, SetSearchQuery, ToggleSelect, ToggleSelectAll} from '@feature/application/actions/application-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {ApplicationSearchDatasource} from '@service/application/application-search-datasource';

const ownerRoles = [RoleType.ROLE_CREATE_APPLICATION,
                      RoleType.ROLE_PROCESS_APPLICATION,
                      RoleType.ROLE_DECISION];

@Component({
  selector: 'search',
  templateUrl: './search.component.html'
})
export class SearchComponent implements OnInit, OnDestroy {

  displayedColumns = [
    'selected', 'owner.realName', 'applicationId', 'name', 'type', 'status', 'project.identifier',
    'customers.applicant.customer.name', 'locations.address', 'locations.cityDistrictId',
    'receivedTime', 'startTime', 'endTime'
  ];

  queryForm: UntypedFormGroup;
  applications: Array<Application>;
  owners: Observable<Array<User>>;
  districts: Observable<Array<CityDistrict>>;
  applicationStatusStrings = searchable;
  applicationTypeStrings = Object.keys(ApplicationType)
    .sort(ArrayUtil.naturalSortTranslated(['application.type'], (type: string) => type));
  dataSource: ApplicationSearchDatasource;
  allSelected$: Observable<boolean>;
  someSelected$: Observable<boolean>;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  private destroy = new Subject<boolean>();

  constructor(private userService: UserService,
              private store: Store<fromRoot.State>,
              private fb: UntypedFormBuilder) {
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
    this.dataSource = new ApplicationSearchDatasource(this.store, this.paginator, this.sort);

    this.store.pipe(
      select(fromApplication.getApplicationSearchParameters),
      take(1),
      map(query => toForm(query))
    ).subscribe(formValues => this.queryForm.patchValue(formValues));

    this.owners = this.userService.getActiveUsers().pipe(
      map(users => users.filter(user => user.roles.some(r => ownerRoles.includes(RoleType[r])))),
      map(users => users.sort(ArrayUtil.naturalSort((user: User) => user.realName))));
    this.districts = this.store.select(fromRoot.getAllCityDistricts);

    this.allSelected$ = this.store.pipe(select(fromApplication.getAllApplicationsSelected));
    this.someSelected$ = this.store.pipe(
      select(fromApplication.getSelectedApplications),
      map(selected => selected.length > 0)
    );
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  search(): void {
    const query = ApplicationSearchQuery.from(this.queryForm.value);
    this.store.dispatch(new SetSearchQuery(ActionTargetType.Application, query));
  }

  districtName(id: number): Observable<string> {
    return this.store.select(fromRoot.getCityDistrictName(id));
  }

  trackById(index: number, item: Application) {
    return item.id;
  }

  selected(id: number): Observable<boolean> {
    return this.store.pipe(
      select(fromApplication.getSelectedApplications),
      map(selected => selected.indexOf(id) >= 0)
    );
  }

  checkAll(): void {
    this.store.dispatch(new ToggleSelectAll(ActionTargetType.Application));
  }

  checkSingle(id: number) {
    this.store.dispatch(new ToggleSelect(ActionTargetType.Application, id));
  }

  addToBasket(): void {
    this.store.pipe(
      select(fromApplication.getSelectedApplications),
      take(1)
    ).subscribe(selected => {
      this.store.dispatch(new AddMultiple(selected));
      this.store.dispatch(new ClearSelected(ActionTargetType.Application));
    });
  }
}
