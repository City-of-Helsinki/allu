import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs';

import {CurrentUser} from '@service/user/current-user';
import {ApplicationType} from '@model/application/type/application-type';
import {CityDistrict} from '@model/common/city-district';
import {User} from '@model/user/user';
import * as fromRoot from '@feature/allu/reducers';
import {Store} from '@ngrx/store';
import {NumberUtil} from '@util/number.util';
import {UserService} from '@service/user/user-service';
import {filter, map, switchMap} from 'rxjs/internal/operators';
import {ArrayUtil} from '@util/array-util';
import {RoleType} from '@model/user/role-type';
import {FormUtil} from '@util/form.util';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {createTranslated} from '@service/error/error-info';

@Component({
  selector: 'user',
  templateUrl: './user.component.html',
  styleUrls: [
    './user.component.scss'
  ]
})
export class UserComponent implements OnInit {
  userForm: UntypedFormGroup;
  submitted = false;
  applicationTypes = Object.keys(ApplicationType)
    .sort(ArrayUtil.naturalSortTranslated(['application.type'], (type: string) => type));
  roles = Object.keys(RoleType)
    .filter(rt => rt !== RoleType.ROLE_ADMIN)
    .sort(ArrayUtil.naturalSortTranslated(['user.role'], (role: string) => role));
  districts: Observable<Array<CityDistrict>>;

  constructor(private route: ActivatedRoute,
              private userService: UserService,
              private store: Store<fromRoot.State>,
              private fb: UntypedFormBuilder,
              private router: Router,
              private currentUser: CurrentUser) {
    this.userForm = this.fb.group({
      id: undefined,
      userName: ['', Validators.required],
      realName: ['', Validators.required],
      emailAddress: [''],
      phone: [''],
      title: [''],
      isActive: [true],
      allowedApplicationTypes: [[]],
      assignedRoles: [[]],
      cityDistrictIds: [[]]
    });
  }

  ngOnInit(): void {
    this.route.params.pipe(
      map(params => params['id']),
      filter(id => NumberUtil.isDefined(id)),
      switchMap(id => this.userService.getById(id))
    ).subscribe(user => this.userForm.patchValue(user));

    this.districts = this.store.select(fromRoot.getAllCityDistricts);
  }

  save(user: User): void {
    if (this.userForm.valid) {
      this.submitted = true;
      this.userService.save(user).subscribe(savedUser => {
        this.submitted = false;
        this.userForm.patchValue(savedUser);
        this.router.navigate(['/admin/users']);
      });
    } else {
      FormUtil.validateFormFields(this.userForm);
      this.store.dispatch(new NotifyFailure(createTranslated('common.field.faultyValueTitle', 'common.field.faultyValue')));
    }
  }

  canRemoveAdminRole(): Observable<boolean> {
    return this.currentUser.user.pipe(
      map(u => u.isAdmin && u.userName !== this.userForm.value.userName)
    );
  }
}
