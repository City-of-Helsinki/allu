import {WorkQueueComponent} from '@feature/supervision-workqueue/workqueue.component';
import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {Component, DebugElement} from '@angular/core';
import {SupervisionWorkItemStoreMock} from './supervision-work-item-store.mock';
import {SupervisionWorkItemStore} from '@feature/supervision-workqueue/supervision-work-item-store';
import {AvailableToDirective} from '@service/authorization/available-to.directive';
import {availableToDirectiveMockMeta, CurrentUserMock, NotificationServiceMock, UserServiceMock} from '../../mocks';
import {CurrentUser} from '@service/user/current-user';
import {MatDialog} from '@angular/material';
import {SupervisionWorkItem} from '@model/application/supervision/supervision-work-item';
import {FormsModule} from '@angular/forms';
import {AlluCommonModule} from '@feature/common/allu-common.module';
import {OwnerModalModule} from '@feature/common/ownerModal/owner-modal.module';
import {NotificationService} from '@feature/notification/notification.service';
import {getButtonWithText} from '../../selector-helpers';
import {Page} from '@model/common/page';
import {RouterTestingModule} from '@angular/router/testing';
import {UserService} from '@service/user/user-service';

const defaultItems = [
  new SupervisionWorkItem(1),
  new SupervisionWorkItem(2)
];

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
  let store: SupervisionWorkItemStoreMock;
  let notification: NotificationServiceMock;
  let dialog: MatDialog;
  let de: DebugElement;
  const currentUserMock = CurrentUserMock.create(true, true);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
        RouterTestingModule,
        AlluCommonModule,
        OwnerModalModule
      ],
      declarations: [
        WorkQueueComponent,
        MockWorkqueueContentComponent,
        MockWorkqueueFilterComponent
      ],
      providers: [
        {provide: SupervisionWorkItemStore, useClass: SupervisionWorkItemStoreMock},
        {provide: CurrentUser, useValue: currentUserMock},
        {provide: UserService, useClass: UserServiceMock},
        {provide: NotificationService, useClass: NotificationServiceMock},
        MatDialog
      ]
    })
      .overrideDirective(AvailableToDirective, availableToDirectiveMockMeta(currentUserMock))
      .compileComponents();
  }));

  beforeEach(() => {
    store = TestBed.get(SupervisionWorkItemStore) as SupervisionWorkItemStoreMock;
    dialog = TestBed.get(MatDialog);
    notification = TestBed.get(NotificationService) as NotificationServiceMock;
    fixture = TestBed.createComponent(WorkQueueComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;

    store.changeSubject.next({...store.changeSubject.getValue(), page: new Page(defaultItems)});
    comp.ngOnInit();
    fixture.detectChanges();
  });

  afterEach(() => {
    comp.ngOnDestroy();
  });

  it('should disable owner change buttons when no item is selected', fakeAsync(() => {
    de.queryAll(By.css('.mat-raised-button'))
      .map(btn => btn.nativeElement)
      .forEach(btn => expect(btn.disabled).toEqual(true));
  }));

  it('should enable owner change buttons when item is selected', fakeAsync(() => {
    store.changeSubject.next({...store.changeSubject.getValue(), selectedItems: [defaultItems[0].id]});
    fixture.detectChanges();
    tick();
    de.queryAll(By.css('.mat-raised-button'))
      .map(btn => btn.nativeElement)
      .forEach(btn => expect(btn.disabled).toEqual(false));
  }));

  it('should react changing items to self', fakeAsync(() => {
    spyOn(store, 'changeHandlerForSelected').and.callThrough();
    spyOn(notification, 'translateSuccess').and.stub();
    store.changeSubject.next({...store.changeSubject.getValue(), selectedItems: [defaultItems[0].id]});
    fixture.detectChanges();
    tick();

    getButtonWithText(de, 'OMAKSI').click();
    fixture.detectChanges();
    tick();

    const myself = currentUserMock.user$.getValue();
    expect(store.changeHandlerForSelected).toHaveBeenCalledWith(myself.id);
  }));

  it('should open owner modal', fakeAsync(() => {
    store.changeSubject.next({...store.changeSubject.getValue(), selectedItems: [defaultItems[0].id]});
    fixture.detectChanges();
    tick();
    spyOn(dialog, 'open').and.callThrough();

    getButtonWithText(de, 'SIIRRÄ').click();
    fixture.detectChanges();
    tick();
    expect(dialog.open).toHaveBeenCalledTimes(1);
  }));
});
