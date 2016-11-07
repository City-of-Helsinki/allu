import {Component, OnInit, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {FormGroup, FormControl, FormBuilder, Validators} from '@angular/forms';

import {User} from '../../../model/common/user';
import {UserHub} from '../../../service/user/user-hub';
import {translations} from '../../../util/translations';
import {CurrentUser} from '../../../service/user/current-user';
import {Some} from '../../../util/option';
import {MaterializeUtil} from '../../../util/materialize.util';

declare var Materialize: any;

@Component({
  selector: 'user',
  template: require('./user.component.html'),
  styles: [
    require('./user.component.scss')
  ]
})
export class UserComponent implements OnInit, AfterViewInit {
  userForm: FormGroup;
  submitted = false;
  translations = translations;
  applicationTypes = ['OUTDOOREVENT', 'PROMOTION'];
  roles = [
    'ROLE_CREATE_APPLICATION',
    'ROLE_PROCESS_APPLICATION',
    'ROLE_DECISION',
    'ROLE_SUPERVISE',
    'ROLE_INVOICING',
    'ROLE_VIEW'
  ];

  constructor(private route: ActivatedRoute, private userHub: UserHub, private fb: FormBuilder, private router: Router) {
    this.userForm = fb.group({
      id: undefined,
      userName: ['', Validators.required],
      realName: ['', Validators.required],
      emailAddress: [''],
      title: [''],
      isActive: [true],
      allowedApplicationTypes: [[]],
      assignedRoles: [[]]
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      Some(params['userName']).do(userName => {
        this.userHub.getUser(userName).subscribe(user => {
          this.userForm.setValue(user);
        });
      });
    });
  }

  ngAfterViewInit(): void {
    MaterializeUtil.updateTextFields(50);
  }

  save(user: UserForm): void {
    this.submitted = true;
    console.log('user.id', user.id);
    this.userHub.saveUser(user).subscribe(savedUser => {
      this.submitted = false;
      this.userForm.setValue(savedUser);
      this.router.navigate(['/admin/user-list']);
    });
  }

  canRemoveAdminRole(): boolean {
    return CurrentUser.isAdmin() && this.editingOther();
  }

  private editingOther(): boolean {
    return CurrentUser.userName().map(current => current !== this.userForm.value.userName).orElse(false);
  }
}

interface UserForm {
  id: number;
  userName: string;
  realName: string;
  emailAddress: string;
  title: string;
  isActive: boolean;
  allowedApplicationTypes: Array<string>;
  assignedRoles: Array<string>;
}
