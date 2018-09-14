import {DebugElement} from '@angular/core';
import {FormBuilder, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {ApplicationStoreMock, availableToDirectiveMockMeta, CurrentUserMock} from '../../../mocks';
import {AvailableToDirective} from '@service/authorization/available-to.directive';
import {SupervisionTaskComponent} from '@feature/application/supervision/supervision-task.component';
import {ApplicationStore} from '@service/application/application-store';
import {CurrentUser} from '@service/user/current-user';
import {ComplexValidator} from '@util/complex-validator';
import {User} from '@model/user/user';
import {SupervisionTaskType} from '@model/application/supervision/supervision-task-type';
import {findTranslation} from '@util/translations';
import {UserHub} from '@service/user/user-hub';
import {UserSearchCriteria} from '@model/user/user-search-criteria';
import {SupervisionTaskStatusType} from '@model/application/supervision/supervision-task-status-type';
import {of} from 'rxjs/index';
import * as fromRoot from '@feature/allu/reducers';
import {combineReducers, Store, StoreModule} from '@ngrx/store';
import * as fromSupervisionTask from '@feature/application/supervision/reducers';
import * as fromApplication from '@feature/application/reducers';
import {Remove, Save} from '@feature/application/supervision/actions/supervision-task-actions';
import {Application} from '@model/application/application';
import * as ApplicationActions from '@feature/application/actions/application-actions';
import {SupervisionTaskForm} from '@feature/application/supervision/supervision-task-form';

const supervisor = new User(2, 'supervisor', 'supervisor');

const taskForm = {
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
  result: [undefined]
};

const validTask: SupervisionTaskForm = {
  type: SupervisionTaskType.SUPERVISION,
  status: SupervisionTaskStatusType.OPEN,
  creatorId: undefined,
  ownerId: supervisor.id,
  plannedFinishingTime: new Date(),
  description: 'some description here'
};

const currentApplication = new Application(1);

class UserHubMock {
  searchUsers(criteria: UserSearchCriteria) { return of([]); }
}

describe('SupervisionTaskComponent', () => {
  let comp: SupervisionTaskComponent;
  let fixture: ComponentFixture<SupervisionTaskComponent>;
  let store: Store<fromRoot.State>;
  let de: DebugElement;
  const currentUserMock = CurrentUserMock.create(true, true);
  let userHub: UserHubMock;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule,
        FormsModule,
        ReactiveFormsModule,
        StoreModule.forRoot({
          'supervisionTasks': combineReducers(fromSupervisionTask.reducers),
          'application': combineReducers(fromApplication.reducers)
        })
      ],
      declarations: [
        SupervisionTaskComponent
      ],
      providers: [
        {provide: ApplicationStore, useClass: ApplicationStoreMock},
        {provide: CurrentUser, useValue: currentUserMock},
        {provide: UserHub, useClass: UserHubMock},
      ]
    })
      .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
      .compileComponents();
  }));

  beforeEach(() => {
    store = TestBed.get(Store);
    userHub = TestBed.get(UserHub) as UserHubMock;
    fixture = TestBed.createComponent(SupervisionTaskComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;

    store.dispatch(new ApplicationActions.LoadSuccess(currentApplication));
    comp.form = new FormBuilder().group(taskForm);
    comp.supervisors = [supervisor];
    comp.ngOnInit();
    fixture.detectChanges();
  });

  it('should initialize', () => {
    expect(de.query(By.css('form'))).toBeDefined();
  });

  it('should keep form enabled for new task', () => {
    expect(comp.form.enabled).toEqual(true, 'Form for new task was disabled');
  });

  it('should disable form for existing task', fakeAsync(() => {
    patchValueAndInit({id: 1});
    expect(comp.form.disabled).toEqual(true, 'Form for existing task was enabled');
  }));

  it('should save valid task', fakeAsync(() => {
    spyOn(store, 'dispatch').and.callThrough();

    patchValueAndInit(validTask);
    const saveBtn = de.query(By.css('#save')).nativeElement;
    saveBtn.click();
    detectAndTick();
    const expectedTask = SupervisionTaskForm.to(comp.form.value);
    expectedTask.applicationId = currentApplication.id;
    expect(store.dispatch).toHaveBeenCalledWith(new Save(expectedTask));
  }));

  it('should change to edit mode', fakeAsync(() => {
    patchValueAndInit({id: 1});
    const editBtn = de.query(By.css('#edit')).nativeElement;
    expect(comp.form.disabled).toEqual(true, 'Form was enabled');
    expect(editBtn).toBeDefined('No edit button');
    editBtn.click();
    detectAndTick();
    expect(comp.form.enabled).toEqual(true, 'Form was disabled after edit');
  }));

  it('should cancel edit changes', fakeAsync(() => {
    patchValueAndInit({id: 1});
    const editBtn = de.query(By.css('#edit')).nativeElement;
    editBtn.click();
    detectAndTick();
    const valueBeforeReset = comp.form.value;
    comp.form.patchValue(validTask);
    detectAndTick();
    const cancelBtn = de.query(By.css('#cancel')).nativeElement;
    cancelBtn.click();
    detectAndTick();
    expect(comp.form.value).toEqual(valueBeforeReset, 'Form was not reset correctly');
  }));

  it('should remove new on cancel', fakeAsync(() => {
    const onRemove = comp.onRemove;
    spyOn(onRemove, 'emit');
    patchValueAndInit({});
    const cancelBtn = de.query(By.css('#cancel')).nativeElement;
    cancelBtn.click();
    detectAndTick();
    expect(onRemove.emit).toHaveBeenCalled();
  }));

  it('should remove existing', fakeAsync(() => {
    const onRemove = comp.onRemove;
    spyOn(onRemove, 'emit');
    spyOn(store, 'dispatch').and.callThrough();

    patchValueAndInit({id: 1, creatorId: undefined, status: SupervisionTaskStatusType[SupervisionTaskStatusType.OPEN]});
    const removeBtn = de.query(By.css('#remove')).nativeElement;
    removeBtn.click();
    detectAndTick();

    expect(store.dispatch).toHaveBeenCalledWith(new Remove(1));
    expect(onRemove.emit).toHaveBeenCalled();
  }));

  it('should disallow editing by other users', fakeAsync(() => {
    const myself = new User(1);
    const other = new User(2);
    patchValueAndInit({id: 1, creatorId: myself.id});
    expect(de.queryAll(By.css('.mat-raised-button')).length).toEqual(1); // Only edit button

    spyOn(currentUserMock, 'isCurrentUser').and.returnValue(of(false));
    patchValueAndInit({creatorId: other.id});
    expect(de.queryAll(By.css('.mat-raised-button')).length).toEqual(0);
  }));

  it('should display error when planned finishing time is in the past', fakeAsync(() => {
    const dateInput = de.query(By.css('[formControlName="plannedFinishingTime"]')).nativeElement;
    const date = new Date();
    date.setFullYear(2000);
    dateInput.value = date;
    dateInput.dispatchEvent(new Event('input'));
    dateInput.dispatchEvent(new Event('blur'));
    detectAndTick();
    const error = de.query(By.css('.mat-error')).nativeElement;
    expect(error).toBeDefined();
    expect(error.textContent).toMatch(findTranslation('supervision.task.field.plannedFinishingTimeInThePast'));
  }));

  it('should preset supervisor when creating new task', fakeAsync(() => {
    const preferredSupervisor = new User(52);
    spyOn(userHub, 'searchUsers').and.returnValue(of([preferredSupervisor]));
    patchValueAndInit({});
    expect(comp.form.value.ownerId).toEqual(preferredSupervisor.id);
  }));

  it('should display task result when available', fakeAsync(() => {
    patchValueAndInit({result: 'TestResult'});
    const resultInput = de.query(By.css('[formControlName="result"]')).nativeElement;
    expect(resultInput.value).toEqual('TestResult');
  }));

  it('should show approval buttons only for owner which the task is assigned to', fakeAsync(() => {
    patchValueAndInit({status: SupervisionTaskStatusType[SupervisionTaskStatusType.OPEN]});
    expect(de.query(By.css('#approve'))).toBeDefined();
    expect(de.query(By.css('#reject'))).toBeDefined();

    spyOn(currentUserMock, 'isCurrentUser').and.returnValue(of(false));
    patchValueAndInit({ownerId: 1});
    expect(de.query(By.css('#approve'))).toBeNull();
    expect(de.query(By.css('#reject'))).toBeNull();
  }));

  afterEach(() => {
    comp.ngOnDestroy();
  });

  function patchValueAndInit(val: any): void {
    comp.form.patchValue(val);
    comp.ngOnInit();
    detectAndTick();
  }

  function detectAndTick(): void {
    fixture.detectChanges();
    tick();
  }
});
