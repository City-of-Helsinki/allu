import {Component, DebugElement, Input} from '@angular/core';
import {FormBuilder, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {ApplicationStoreMock, availableToDirectiveMockMeta, CurrentUserMock, UserServiceMock} from '../../../mocks';
import {AvailableToDirective} from '@service/authorization/available-to.directive';
import {SupervisionTaskComponent} from '@feature/application/supervision/supervision-task.component';
import {ApplicationStore} from '@service/application/application-store';
import {CurrentUser} from '@service/user/current-user';
import {ComplexValidator} from '@util/complex-validator';
import {User} from '@model/user/user';
import {SupervisionTaskType} from '@model/application/supervision/supervision-task-type';
import {findTranslation} from '@util/translations';
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
import {ApplicationType} from '@model/application/type/application-type';
import {getButtonWithText} from '../../../selector-helpers';
import {UserService} from '@service/user/user-service';
import {Location} from '@model/common/location';

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
  plannedFinishingTime: [undefined, Validators.required],
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

@Component({
  selector: 'supervision-task-location',
  template: ''
})
class SupervisionTaskLocationMockComponent {
  @Input() taskId = 1;
  @Input() applicationType: ApplicationType;
  @Input() relatedLocation: Location;
  @Input() locations: Location[];
}

describe('SupervisionTaskComponent', () => {
  let comp: SupervisionTaskComponent;
  let fixture: ComponentFixture<SupervisionTaskComponent>;
  let store: Store<fromRoot.State>;
  let de: DebugElement;
  const currentUserMock = CurrentUserMock.create(true, true);
  let userService: UserServiceMock;

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
        SupervisionTaskComponent,
        SupervisionTaskLocationMockComponent
      ],
      providers: [
        {provide: ApplicationStore, useClass: ApplicationStoreMock},
        {provide: CurrentUser, useValue: currentUserMock},
        {provide: UserService, useClass: UserServiceMock},
      ]
    })
      .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
      .compileComponents();
  }));

  beforeEach(() => {
    store = TestBed.get(Store);
    userService = TestBed.get(UserService) as UserServiceMock;
    fixture = TestBed.createComponent(SupervisionTaskComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;

    currentApplication.locations = [new Location(1)];
    store.dispatch(new ApplicationActions.LoadSuccess(currentApplication));
    comp.form = new FormBuilder().group(taskForm);
    comp.supervisors = [supervisor];
    currentApplication.type = ApplicationType.EVENT;
    comp.application = currentApplication;
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
    const saveBtn = getButtonWithText(de, findTranslation('common.button.save'));
    saveBtn.click();
    detectAndTick();
    const expectedTask = SupervisionTaskForm.to(comp.form.value);
    expectedTask.applicationId = currentApplication.id;
    expect(store.dispatch).toHaveBeenCalledWith(new Save(expectedTask));
  }));

  it('should change to edit mode', fakeAsync(() => {
    patchValueAndInit({id: 1});
    const editBtn = getButtonWithText(de, findTranslation('common.button.edit'));
    expect(comp.editing).toEqual(false, 'Form was enabled');
    expect(editBtn).toBeDefined('No edit button');
    editBtn.click();
    detectAndTick();
    expect(comp.editing).toEqual(true, 'Form was disabled after edit');

  }));

  it('should cancel edit changes', fakeAsync(() => {
    patchValueAndInit({id: 1});
    const editBtn = getButtonWithText(de, findTranslation('common.button.edit'));
    editBtn.click();
    detectAndTick();
    const valueBeforeReset = comp.form.value;
    comp.form.patchValue(validTask);
    detectAndTick();
    const cancelBtn = getButtonWithText(de, findTranslation('common.button.cancel'));
    cancelBtn.click();
    detectAndTick();
    expect(comp.form.getRawValue()).toEqual(valueBeforeReset, 'Form was not reset correctly');
  }));

  it('should remove new on cancel', fakeAsync(() => {
    const onRemove = comp.onRemove;
    spyOn(onRemove, 'emit');
    patchValueAndInit({});
    const cancelBtn = getButtonWithText(de, findTranslation('common.button.cancel'));
    cancelBtn.click();
    detectAndTick();
    expect(onRemove.emit).toHaveBeenCalled();
  }));

  it('should remove existing', fakeAsync(() => {
    const onRemove = comp.onRemove;
    spyOn(onRemove, 'emit');
    spyOn(store, 'dispatch').and.callThrough();

    patchValueAndInit({id: 1, creatorId: undefined, status: SupervisionTaskStatusType.OPEN});
    const removeBtn = getButtonWithText(de, findTranslation('common.button.remove'));
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

  it('should display error when planned finishing time is not set', fakeAsync(() => {
    const dateInput = de.query(By.css('[formControlName="plannedFinishingTime"]')).nativeElement;
    dateInput.value = undefined;
    dateInput.dispatchEvent(new Event('input'));
    dateInput.dispatchEvent(new Event('blur'));
    detectAndTick();
    const error = de.query(By.css('.mat-error')).nativeElement;
    expect(error).toBeDefined();
    expect(error.textContent).toMatch(findTranslation('supervision.task.field.plannedFinishingTimeMissing'));
  }));

  it('should preset supervisor when creating new task', fakeAsync(() => {
    const preferredSupervisor = new User(52);
    spyOn(userService, 'search').and.returnValue(of([preferredSupervisor]));
    patchValueAndInit({});
    expect(comp.form.value.ownerId).toEqual(preferredSupervisor.id);
  }));

  it('should display task result when available', fakeAsync(() => {
    patchValueAndInit({result: 'TestResult'});
    const resultInput = de.query(By.css('[formControlName="result"]')).nativeElement;
    expect(resultInput.value).toEqual('TestResult');
  }));

  it('should show approval buttons only for owner which the task is assigned to', fakeAsync(() => {
    patchValueAndInit({status: SupervisionTaskStatusType.OPEN});
    expect(de.query(By.css('#approve'))).toBeDefined();
    expect(de.query(By.css('#reject'))).toBeDefined();

    spyOn(currentUserMock, 'isCurrentUser').and.returnValue(of(false));
    patchValueAndInit({ownerId: 1});
    expect(de.query(By.css('#approve'))).toBeNull();
    expect(de.query(By.css('#reject'))).toBeNull();
  }));

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
