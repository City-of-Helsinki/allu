import {DebugElement} from '@angular/core';
import {FormBuilder, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {SupervisionTaskStore} from '../../../../src/app/service/supervision/supervision-task-store';
import {AlluCommonModule} from '../../../../src/app/feature/common/allu-common.module';
import {ApplicationStoreMock, availableToDirectiveMockMeta, CurrentUserMock} from '../../../mocks';
import {AvailableToDirective} from '../../../../src/app/service/authorization/available-to.directive';
import {SupervisionTask} from '../../../../src/app/model/application/supervision/supervision-task';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';
import {SupervisionTaskComponent} from '../../../../src/app/feature/application/supervision/supervision-task.component';
import {ApplicationStore} from '../../../../src/app/service/application/application-store';
import {CurrentUser} from '../../../../src/app/service/user/current-user';
import {ComplexValidator} from '../../../../src/app/util/complex-validator';
import {User} from '../../../../src/app/model/user/user';
import {SupervisionTaskType} from '../../../../src/app/model/application/supervision/supervision-task-type';
import {NotificationService} from '../../../../src/app/service/notification/notification.service';
import {ErrorInfo} from '../../../../src/app/service/ui-state/error-info';
import {HttpResponse, HttpStatus} from '../../../../src/app/util/http-response';
import {findTranslation} from '../../../../src/app/util/translations';
import {UserHub} from '../../../../src/app/service/user/user-hub';
import {UserSearchCriteria} from '../../../../src/app/model/user/user-search-criteria';
import {SupervisionTaskStatusType} from '../../../../src/app/model/application/supervision/supervision-task-status-type';

const supervisor = new User(2, 'supervisor', 'supervisor');

const taskForm = {
  id: [undefined],
  applicationId: [undefined],
  type: [undefined, Validators.required],
  creatorId: [undefined],
  creatorName: [undefined],
  handlerId: [undefined, Validators.required],
  handlerName: [undefined],
  creationTime: [undefined],
  plannedFinishingTime: [undefined, [Validators.required, ComplexValidator.inThePast]],
  actualFinishingTime: [undefined],
  status: [undefined],
  description: [undefined],
  result: [undefined]
};

const validTask = {
  type: SupervisionTaskType[SupervisionTaskType.SUPERVISION],
  status: SupervisionTaskStatusType[SupervisionTaskStatusType.OPEN],
  creatorId: undefined,
  handlerId: supervisor.id,
  plannedFinishingTime: new Date(),
  description: 'some description here'
};

class UserHubMock {
  searchUsers(criteria: UserSearchCriteria) { return Observable.of([]); }
}

class SupervisionTaskStoreMock {
  public tasks$ = new Subject<Array<SupervisionTask>>();

  get tasks(): Observable<Array<SupervisionTask>> {
    return this.tasks$.asObservable();
  }

  saveTask(applicationId: number, task: SupervisionTask): Observable<SupervisionTask> {
    return Observable.of(task);
  }

  removeTask(applicationId: number, taskId: number): Observable<HttpResponse> {
    return Observable.of(new HttpResponse(HttpStatus.OK));
  }
}

describe('SupervisionTaskComponent', () => {
  let comp: SupervisionTaskComponent;
  let fixture: ComponentFixture<SupervisionTaskComponent>;
  let supervisionTaskStore: SupervisionTaskStoreMock;
  let de: DebugElement;
  const currentUserMock = CurrentUserMock.create(true, true);
  let userHub: UserHubMock;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        AlluCommonModule,
        FormsModule,
        ReactiveFormsModule
      ],
      declarations: [
        SupervisionTaskComponent
      ],
      providers: [
        {provide: ApplicationStore, useClass: ApplicationStoreMock},
        {provide: SupervisionTaskStore, useClass: SupervisionTaskStoreMock},
        {provide: CurrentUser, useValue: currentUserMock},
        {provide: UserHub, useClass: UserHubMock}
      ]
    })
      .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
      .compileComponents();
  }));

  beforeEach(() => {
    supervisionTaskStore = TestBed.get(SupervisionTaskStore) as SupervisionTaskStoreMock;
    userHub = TestBed.get(UserHub) as UserHubMock;
    fixture = TestBed.createComponent(SupervisionTaskComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;

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
    spyOn(supervisionTaskStore, 'saveTask').and.callThrough();
    spyOn(NotificationService, 'translateMessage');

    patchValueAndInit(validTask);
    const saveBtn = de.query(By.css('#save')).nativeElement;
    saveBtn.click();
    detectAndTick();
    expect(supervisionTaskStore.saveTask).toHaveBeenCalled();
    expect(NotificationService.translateMessage).toHaveBeenCalled();
  }));

  it('should handle save error', fakeAsync(() => {
    const errorInfo = ErrorInfo.of(new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR), 'expected');
    spyOn(supervisionTaskStore, 'saveTask').and.returnValue(Observable.throw(errorInfo));
    spyOn(NotificationService, 'translateError');

    patchValueAndInit(validTask);
    const saveBtn = de.query(By.css('#save')).nativeElement;
    saveBtn.click();
    detectAndTick();
    expect(supervisionTaskStore.saveTask).toHaveBeenCalled();
    expect(NotificationService.translateError).toHaveBeenCalled();
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
    spyOn(NotificationService, 'translateMessage');
    spyOn(supervisionTaskStore, 'removeTask').and.returnValue(Observable.of(new HttpResponse(HttpStatus.OK)));

    patchValueAndInit({id: 1, creatorId: undefined, status: SupervisionTaskStatusType[SupervisionTaskStatusType.OPEN]});
    const removeBtn = de.query(By.css('#remove')).nativeElement;
    removeBtn.click();
    detectAndTick();

    expect(supervisionTaskStore.removeTask).toHaveBeenCalled();
    expect(NotificationService.translateMessage).toHaveBeenCalled();
    expect(onRemove.emit).toHaveBeenCalled();
  }));

  it('should handle remove failure', fakeAsync(() => {
    const errorInfo = ErrorInfo.of(new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR), 'expected');
    const onRemove = comp.onRemove;
    spyOn(NotificationService, 'translateError');
    spyOn(onRemove, 'emit');
    spyOn(supervisionTaskStore, 'removeTask').and.returnValue(Observable.throw(errorInfo));

    patchValueAndInit({id: 1, creatorId: undefined, status: SupervisionTaskStatusType[SupervisionTaskStatusType.OPEN]});
    const removeBtn = de.query(By.css('#remove')).nativeElement;
    removeBtn.click();
    detectAndTick();

    expect(supervisionTaskStore.removeTask).toHaveBeenCalled();
    expect(NotificationService.translateError).toHaveBeenCalled();
    expect(onRemove.emit).not.toHaveBeenCalled();
  }));

  it('should disallow editing by other users', fakeAsync(() => {
    const myself = new User(1);
    const other = new User(2);
    patchValueAndInit({id: 1, creatorId: myself.id});
    expect(de.queryAll(By.css('.mat-raised-button')).length).toEqual(1); // Only edit button

    spyOn(currentUserMock, 'isCurrentUser').and.returnValue(Observable.of(false));
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
    spyOn(userHub, 'searchUsers').and.returnValue(Observable.of([preferredSupervisor]));
    patchValueAndInit({});
    expect(comp.form.value.handlerId).toEqual(preferredSupervisor.id);
  }));

  it('should display task result when available', fakeAsync(() => {
    patchValueAndInit({result: 'TestResult'});
    const resultInput = de.query(By.css('[formControlName="result"]')).nativeElement;
    expect(resultInput.value).toEqual('TestResult');
  }));

  it('should show approval buttons only for handler which the task is assigned to', fakeAsync(() => {
    patchValueAndInit({status: SupervisionTaskStatusType[SupervisionTaskStatusType.OPEN]});
    expect(de.query(By.css('#approve'))).toBeDefined();
    expect(de.query(By.css('#reject'))).toBeDefined();

    spyOn(currentUserMock, 'isCurrentUser').and.returnValue(Observable.of(false));
    patchValueAndInit({handlerId: 1});
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
