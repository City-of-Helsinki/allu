import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormArray, FormBuilder, Validators} from '@angular/forms';
import {FormUtil} from '../../../util/form.util';
import {UserHub} from '../../../service/user/user-hub';
import {User} from '../../../model/user/user';
import {RoleType} from '../../../model/user/role-type';
import {TimeUtil} from '../../../util/time.util';
import {SupervisionTask} from '../../../model/application/supervision/supervision-task';
import {SupervisionTaskStore} from '../../../service/supervision/supervision-task-store';
import {ComplexValidator} from '../../../util/complex-validator';
import {SupervisionTaskForm} from './supervision-task-form';
import {Subscription} from 'rxjs';
import {map} from 'rxjs/internal/operators';


@Component({
  selector: 'supervision',
  templateUrl: './supervision.component.html',
  styleUrls: ['./supervision.component.scss']
})
export class SupervisionComponent implements OnInit, OnDestroy {
  supervisionTasks: FormArray;
  supervisors: Array<User> = [];

  private supervisionTaskSubscription: Subscription;

  constructor(private fb: FormBuilder,
              private supervisionTaskStore: SupervisionTaskStore,
              private userHub: UserHub) {
    this.supervisionTasks = this.fb.array([]);
  }

  ngOnInit(): void {
    this.supervisionTaskSubscription = this.supervisionTaskStore.tasks.pipe(
      map(tasks => tasks.sort((l, r) => TimeUtil.compareTo(r.creationTime, l.creationTime))) // latest first
    ).subscribe(tasks => {
        FormUtil.clearArray(this.supervisionTasks);
        tasks.forEach(task => this.addNew(task));
      });

    this.userHub.getByRole(RoleType.ROLE_SUPERVISE).subscribe(users => this.supervisors = users);
  }

  ngOnDestroy(): void {
    this.supervisionTaskSubscription.unsubscribe();
  }

  addNew(task: SupervisionTask = new SupervisionTask()): void {
    task.plannedFinishingTime = task.plannedFinishingTime || new Date();
    const formGroup = this.fb.group({
      id: [undefined],
      applicationId: [undefined],
      type: [undefined, Validators.required],
      creatorId: [undefined],
      creatorName: [undefined],
      ownerId: [undefined, Validators.required],
      ownerName: [undefined],
      creationTime: [undefined],
      plannedFinishingTime: [undefined, [Validators.required, ComplexValidator.inThePast]],
      actualFinishingTime: [undefined],
      status: [undefined],
      description: [undefined],
      result: [undefined],
      automatic: [undefined]
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
