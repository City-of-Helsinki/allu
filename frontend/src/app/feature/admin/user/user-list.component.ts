import {Component, Inject, LOCALE_ID, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {User} from '@model/user/user';
import {UserService} from '@service/user/user-service';
import * as fromRoot from '@feature/allu/reducers';
import {Store} from '@ngrx/store';
import {MatPaginator, MatSort, MatTableDataSource} from '@angular/material';
import {formatDate} from '@angular/common';
import {findTranslation, translateArray} from '@util/translations';
import {map, withLatestFrom} from 'rxjs/operators';
import {flattenToString, StringUtil} from '@util/string.util';
import {Dictionary} from '@ngrx/entity';
import {CityDistrict} from '@model/common/city-district';
import {AlluTableDataSource} from '@feature/common/table/allu-table-data-source';

@Component({
  selector: 'user-list',
  templateUrl: './user-list.component.html',
  styleUrls: []
})
export class UserListComponent implements OnInit {

  dataSource: AlluTableDataSource<UserElement>;
  displayedColumns: string[] = ['userName', 'realName', 'lastLogin', 'isActive', 'roles', 'applicationTypes', 'cityDistricts'];

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(private userService: UserService,
              private router: Router,
              private store: Store<fromRoot.State>,
              @Inject(LOCALE_ID) private localeId: string) {
    this.dataSource = new AlluTableDataSource([], {
      caseInsensitiveFields: [
        'userName',
        'realName'
      ]
    });
  }

  ngOnInit(): void {
    this.userService.getAllUsers().pipe(
      withLatestFrom(this.store.select(fromRoot.getCityDistrictEntities)),
      map(([users, cityDistricts]) => users.map(user => this.toUserElement(user, cityDistricts)))
    ).subscribe(users =>  {
      this.dataSource.data = users;
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    });

    this.dataSource.filterPredicate = (user: UserElement, filter: string) => {
      const filterValues = filter.trim().toLowerCase().split(' ');
      const flatUser = flattenToString(user);
      return filterValues.reduce((matches, cur) => matches && (StringUtil.isEmpty(cur) || flatUser.includes(cur)), true);
    };
  }

  showUser(user: UserElement): void {
    this.router.navigate(['/admin/users', user.id]);
  }

  trackById(index: number, item: User) {
    return item.id;
  }

  applyFilter(filterValue: string): void {
    this.dataSource.filter = filterValue;
  }

  toUserElement(user: User, cityDistricts: Dictionary<CityDistrict>): UserElement {
    return {
      id: user.id,
      userName: user.userName,
      realName: user.realName,
      lastLogin: user.lastLogin ? formatDate(user.lastLogin, 'short', this.localeId) : undefined,
      isActive: findTranslation(['common.boolean', user.isActive.toString()]),
      roles: translateArray('user.role', user.assignedRoles),
      applicationTypes: translateArray('application.type', user.allowedApplicationTypes),
      cityDistricts: user.cityDistrictIds.map(id => cityDistricts[id])
        .filter(cd => !!cd)
        .map(cd => cd.name)
    };
  }
}

export interface UserElement {
  id: number;
  userName: string;
  realName: string;
  lastLogin: string;
  isActive: string;
  roles: string[];
  applicationTypes: string[];
  cityDistricts: string[];
}
