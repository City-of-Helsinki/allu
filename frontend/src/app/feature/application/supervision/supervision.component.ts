import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormArray, FormBuilder, Validators} from '@angular/forms';
import {FormUtil} from '@util/form.util';
import {User} from '@model/user/user';
import {RoleType} from '@model/user/role-type';
import {SupervisionTask} from '@model/application/supervision/supervision-task';
import {SupervisionTaskForm} from './supervision-task-form';
import {Observable, Subscription} from 'rxjs';
import {select, Store} from '@ngrx/store';
import * as fromSupervisionTask from './reducers';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {Application} from '@model/application/application';
import {UserService} from '@service/user/user-service';
import {DECISION_BLOCKING_TAGS} from '@model/application/tag/application-tag-type';


@Component({
  selector: 'supervision',
  templateUrl: './supervision.component.html',
  styleUrls: ['./supervision.component.scss']
})
export class SupervisionComponent implements OnInit, OnDestroy {
  supervisionTasks: FormArray;
  supervisors: Array<User> = [];
  application$: Observable<Application>;
  hasDisablingTags$: Observable<boolean>;
  loading$: Observable<boolean>;

  private supervisionTaskSubscription: Subscription;

  constructor(private fb: FormBuilder,
              private userService: UserService,
              private store: Store<fromRoot.State>) {
    this.supervisionTasks = this.fb.array([]);
  }

  ngOnInit(): void {
    this.supervisionTaskSubscription = this.store.pipe(select(fromSupervisionTask.getAllSupervisionTasks)).subscribe(tasks => {
      FormUtil.clearArray(this.supervisionTasks);
      tasks.forEach(task => this.addNew(task));
    });
    this.application$ = this.store.pipe(select(fromApplication.getCurrentApplication));
    this.loading$ = this.store.pipe(select(fromSupervisionTask.getLoading));

    this.userService.getByRole(RoleType.ROLE_SUPERVISE).subscribe(users => this.supervisors = users);
    this.hasDisablingTags$ = this.store.pipe(
      select(fromApplication.hasTags(DECISION_BLOCKING_TAGS))
    );
  }

  ngOnDestroy(): void {
    this.supervisionTaskSubscription.unsubscribe();
  }

  addNew(task: SupervisionTask = new SupervisionTask()): void {
    task = {
      ...task,
      plannedFinishingTime: task?.plannedFinishingTime || new Date()
    };

    const formGroup = this.fb.group({
      id: [undefined],
      applicationId: [undefined],
      type: [undefined, Validators.required],
      creatorId: [undefined],
      creatorName: [undefined],
      ownerId: [undefined, Validators.required],
      ownerName: [undefined],
      creationTime: [undefined],
      plannedFinishingTime: [undefined, Validators.required],
      actualFinishingTime: [undefined],
      status: [undefined],
      description: [undefined],
      result: [undefined],
      automatic: [undefined],
      compactionAndBearingCapacityMeasurement: [undefined],
      qualityAssuranceTest: [undefined],
      locationId: [undefined],
      supervisedLocations: [[]]
    });

    formGroup.patchValue(SupervisionTaskForm.from(task));

    if (task.id === undefined) {
      this.supervisionTasks.insert(0, formGroup);
    } else {
      this.supervisionTasks.push(formGroup);
    }
  }

  remove(index: number): void {
    this.supervisionTasks.removeAt(index);
  }
}
