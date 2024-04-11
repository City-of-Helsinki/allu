import {WorkQueueComponent} from '@feature/supervision-workqueue/workqueue.component';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {Component, DebugElement} from '@angular/core';
import {AvailableToDirective} from '@service/authorization/available-to.directive';
import {availableToDirectiveMockMeta, CurrentUserMock, UserServiceMock} from '../../mocks';
import {CurrentUser} from '@service/user/current-user';
import {MatDialog} from '@angular/material/dialog';
import {SupervisionWorkItem} from '@model/application/supervision/supervision-work-item';
import {FormsModule} from '@angular/forms';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {OwnerModalModule} from '@feature/common/ownerModal/owner-modal.module';
import {getButtonWithText} from '../../selector-helpers';
import {Page} from '@model/common/page';
import {RouterTestingModule} from '@angular/router/testing';
import {UserService} from '@service/user/user-service';
import {combineReducers, Store, StoreModule} from '@ngrx/store';
import * as fromSupervisionWorkQueue from '@feature/supervision-workqueue/reducers';
import * as fromAuth from '@feature/auth/reducers';
import {SearchSuccess, ToggleSelect} from '@feature/application/supervision/actions/supervision-task-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {ChangeOwner} from '@feature/application/supervision/actions/supervision-task-actions';

const defaultItems = new Page([
  new SupervisionWorkItem(1),
  new SupervisionWorkItem(2)
]);

@Component({
  selector: 'supervision-workqueue-content',
  template: ''
})
class MockWorkqueueContentComponent {
}

@Component({
  selector: 'supervision-workqueue-filter',
  template: ''
})
class MockWorkqueueFilterComponent {
}

describe('SupervisionWorkqueueComponent', () => {
  let comp: WorkQueueComponent;
  let fixture: ComponentFixture<WorkQueueComponent>;
  let store: Store<fromSupervisionWorkQueue.State>;
  let dialog: MatDialog;
  let de: DebugElement;
  const currentUserMock = CurrentUserMock.create(true, true);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
        RouterTestingModule,
        AlluCommonModule,
        OwnerModalModule,
        StoreModule.forRoot({
          'supervisionWorkQueue': combineReducers(fromSupervisionWorkQueue.reducers),
          'auth': combineReducers(fromAuth.reducers)
        })
      ],
      declarations: [
        WorkQueueComponent,
        MockWorkqueueContentComponent,
        MockWorkqueueFilterComponent
      ],
      providers: [
        {provide: CurrentUser, useValue: currentUserMock},
        {provide: UserService, useClass: UserServiceMock},
        MatDialog
      ]
    })
      .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
      .compileComponents();
  }));

  beforeEach(() => {
    store = TestBed.get(Store);
    dialog = TestBed.get(MatDialog);
    fixture = TestBed.createComponent(WorkQueueComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;

    store.dispatch(new SearchSuccess(ActionTargetType.SupervisionTaskWorkQueue, defaultItems));
    comp.ngOnInit();
    fixture.detectChanges();
  });

  it('should disable owner change buttons when no item is selected', () => {
    de.queryAll(By.css('.mat-raised-button'))
      .map(btn => btn.nativeElement)
      .forEach(btn => expect(btn.disabled).toEqual(true));
  });

  it('should enable owner change buttons when item is selected', () => {
    store.dispatch(new ToggleSelect(ActionTargetType.SupervisionTaskWorkQueue, defaultItems.content[0].id));
    fixture.detectChanges();
    de.queryAll(By.css('.mat-raised-button'))
      .map(btn => btn.nativeElement)
      .forEach(btn => expect(btn.disabled).toEqual(false));
  });

  it('should react changing items to self', () => {
    const taskId = defaultItems.content[0].id;
    spyOn(store, 'dispatch').and.callThrough();
    store.dispatch(new ToggleSelect(ActionTargetType.SupervisionTaskWorkQueue, taskId));
    fixture.detectChanges();

    getButtonWithText(de, 'OMAKSI').click();
    fixture.detectChanges();

    const myself = currentUserMock.user$.getValue();
    expect(store.dispatch).toHaveBeenCalledWith(new ChangeOwner({ownerId: myself.id, taskIds: [taskId]}));
  });

  it('should open owner modal', () => {
    store.dispatch(new ToggleSelect(ActionTargetType.SupervisionTaskWorkQueue, defaultItems.content[0].id));
    fixture.detectChanges();
    spyOn(dialog, 'open').and.callThrough();

    getButtonWithText(de, 'SIIRRÄ').click();
    fixture.detectChanges();
    expect(dialog.open).toHaveBeenCalledTimes(1);
  });
});
