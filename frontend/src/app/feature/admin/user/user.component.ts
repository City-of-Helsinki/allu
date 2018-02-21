import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs/Observable';

import {UserHub} from '../../../service/user/user-hub';
import {CurrentUser} from '../../../service/user/current-user';
import {Some} from '../../../util/option';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicationType} from '../../../model/application/type/application-type';
import {CityDistrict} from '../../../model/common/city-district';
import {User} from '../../../model/user/user';
import {CityDistrictService} from '../../../service/map/city-district.service';

@Component({
  selector: 'user',
  templateUrl: './user.component.html',
  styleUrls: [
    './user.component.scss'
  ]
})
export class UserComponent implements OnInit {
  userForm: FormGroup;
  submitted = false;
  applicationTypes = EnumUtil.enumValues(ApplicationType);
  roles = [
    'ROLE_CREATE_APPLICATION',
    'ROLE_PROCESS_APPLICATION',
    'ROLE_DECISION',
    'ROLE_SUPERVISE',
    'ROLE_INVOICING',
    'ROLE_VIEW'
  ];
  districts: Observable<Array<CityDistrict>>;

  constructor(private route: ActivatedRoute,
              private userHub: UserHub,
              private cityDistrictService: CityDistrictService,
              private fb: FormBuilder,
              private router: Router,
              private currentUser: CurrentUser) {
    this.userForm = this.fb.group({
      id: undefined,
      userName: ['', Validators.required],
      realName: ['', Validators.required],
      emailAddress: [''],
      title: [''],
      isActive: [true],
      allowedApplicationTypes: [[]],
      assignedRoles: [[]],
      cityDistrictIds: [[]]
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      Some(params['userName']).do(userName => {
        this.userHub.getByUsername(userName).subscribe(user => {
          this.userForm.patchValue(user);
        });
      });
    });

    this.districts = this.cityDistrictService.get();
  }

  save(user: User): void {
    this.submitted = true;
    this.userHub.saveUser(user).subscribe(savedUser => {
      this.submitted = false;
      this.userForm.patchValue(savedUser);
      this.router.navigate(['/admin/user-list']);
    });
  }

  canRemoveAdminRole(): Observable<boolean> {
    return this.currentUser.user
      .map(u => u.isAdmin && u.userName !== this.userForm.value.userName);
  }
}
